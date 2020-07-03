package app;

import app.remoteservices.RemoteServices;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.awt.image.CropImageFilter;
import java.awt.image.FilteredImageSource;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static java.awt.Color.yellow;

@SuppressWarnings("serial")
public class PuzzleBoard extends JFrame {

    private final SelectionManager selectionManager = new SelectionManager();
    private final ScheduledExecutorService jobColor = Executors.newSingleThreadScheduledExecutor();

    private final RequestClient requestClient;
    private final String username;

    private List<Tile> tiles = new ArrayList<>();

    final int rows, columns;

    public PuzzleBoard(final int rows, final int columns, final String imagePath, String username) {

        // Dimension Puzzle
        this.rows = rows;
        this.columns = columns;

        // Object Request Client
        this.requestClient = new RequestClient(this.rows, this.columns);
        this.username = username;

        setTitle("Puzzle");
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel board = new JPanel();
        board.setBorder(BorderFactory.createLineBorder(Color.gray));
        board.setLayout(new GridLayout(rows, columns, 0, 0));
        getContentPane().add(board, BorderLayout.CENTER);

        createTiles(imagePath, board);

        // Action close view puzzle
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);

                // There isn't any op to do after delete.
                requestClient.deleteUser(username, null);
            }

            @Override
            public void windowClosed(WindowEvent e) {
                super.windowClosed(e);
            }
        });
    }

    private void createTiles(final String imagePath, final JPanel board) {
        final BufferedImage image;

        // Load image
        try {
            image = ImageIO.read(new File(imagePath));
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Could not load image", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Params image
        final int imageWidth = image.getWidth(null);
        final int imageHeight = image.getHeight(null);

        // API GET for extract position boxes image
        Call<List<Tile>> boxes = RemoteServices.getInstance().getPuzzleService().getMappings();
        boxes.enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<List<Tile>> call, Response<List<Tile>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    int position = 0;

                    for (int i = 0; i < rows; i++) {
                        for (int j = 0; j < columns; j++) {
                            final Image imagePortion = createImage(new FilteredImageSource(image.getSource(),
                                    new CropImageFilter(j * imageWidth / columns,
                                            i * imageHeight / rows,
                                            (imageWidth / columns),
                                            imageHeight / rows)));

                            tiles.add(new Tile(imagePortion, position, response.body().get(position).getOriginalPosition(), username));
                            position++;
                        }
                    }
                    paintPuzzle(board);
                    jobColor.scheduleAtFixedRate(() -> updateCardColor(username), 0, 5000, TimeUnit.MILLISECONDS);
                }
            }

            @Override
            public void onFailure(Call<List<Tile>> call, Throwable t) {
                log(t.getMessage());
            }
        });
    }

    // Paint Puzzle
    private void paintPuzzle(final JPanel board) {
        board.removeAll();

        Collections.sort(tiles);

        tiles.forEach(tile -> {
            final TileButton btn = new TileButton(tile, this.requestClient, this.username);
            tile.setButton(btn);
            board.add(btn);

            btn.setBorder(BorderFactory.createLineBorder(btn.getColor()));
            btn.setColor(btn.getColor());

            // Action Button puzzle
            btn.addActionListener(actionListener -> {

                // Check button is not yellow
                if (!btn.getColor().equals(yellow)) {

                    // Check button is red
                    if (btn.getColor().equals(Color.red)) {
                        btn.releaseBox();
                    } else {
                        // Action for gray button
                        btn.takeBox();

                        // Move boxes
                        selectionManager.selectTile(this.username, this.requestClient, tile, () -> {
                            paintPuzzle(board);
                            checkSolution();
                        });
                    }
                }
            });
        });

        pack();
        setLocationRelativeTo(null);
    }

    /**
     * Update color border box
     * @param username name user
     */
    private void updateCardColor(String username) {

        log("Update color box every 5s");

        this.requestClient.mappingBox(t -> tiles.forEach(tile -> {

            String taker = tile.getTaker();
            if (!taker.equals("") && !taker.equals(username)) {

                this.tiles.stream()
                        .filter(f -> f.getOriginalPosition() == tile.getOriginalPosition())
                        .findFirst()
                        .ifPresent(p ->

                                SwingUtilities.invokeLater(() -> {

                                    log("Box is colored yellow");
                                    p.getButton().setBorder(BorderFactory.createLineBorder(Color.yellow));
                                })
                        );
            }
        }));
    }


    // Check Solution
    private void checkSolution() {
        if (tiles.stream().allMatch(Tile::isInRightPlace)) {
            JOptionPane.showMessageDialog(this, "Puzzle Completed!", "", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void log(String msg) {
        synchronized (System.out) {
            System.out.println("[Info] " + msg);
        }
    }
}