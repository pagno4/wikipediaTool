package app;

import app.remoteservices.RemoteServices;
import app.remoteservices.ReturnMessage;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.List;
import java.util.function.Consumer;

public class RequestClient {

    final String imagePath = "src/main/java/app/bletchley-park-mansion.jpg";
    private final int x;
    private final int y;

    //Documentazione metodi HTTP: https://mkyong.com/java/how-to-send-http-request-getpost-in-java/

    public RequestClient(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void addPlayer(String name, Consumer<String> action) {
        Call<ReturnMessage> add = RemoteServices.getInstance().getPlayersService().addPlayer(name);
        add.enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<ReturnMessage> call, Response<ReturnMessage> response) {
                if (response.isSuccessful() && response.body() != null) {
                    log(response.body().getMessage());
                    action.accept(response.body().getMessage());
                }
            }

            @Override
            public void onFailure(Call<ReturnMessage> call, Throwable t) {
                synchronized (System.out) {
                    System.out.println(t.getMessage());
                }
            }
        });
    }

    public void allUsers(Consumer<List<String>> action) {
        Call<List<String>> res = RemoteServices.getInstance().getPlayersService().allPlayers();
        res.enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<List<String>> call, Response<List<String>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    log(response.body().toString());
                    action.accept(response.body());
                }
            }

            @Override
            public void onFailure(Call<List<String>> call, Throwable t) {

            }
        });
    }

    /**
     * HTTP DELETE for delete user from server
     */
    public void deleteUser(String name, Consumer<String> action) {
        Call<ReturnMessage> call = RemoteServices.getInstance().getPlayersService().deletePlayer(name);
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<ReturnMessage> call, Response<ReturnMessage> response) {
                if (response.isSuccessful() && response.body() != null) {
                    log(response.body().getMessage());
                    action.accept(response.body().getMessage());
                }
            }

            @Override
            public void onFailure(Call<ReturnMessage> call, Throwable t) {
                synchronized (System.out) {
                    System.out.println(t.getMessage());
                }
            }
        });
    }

    /**
     * Start puzzle game
     */
    public void startGame() {
        final PuzzleBoard puzzle = new PuzzleBoard(this.x, this.y, this.imagePath);
        puzzle.setVisible(true);
    }

    private void log(String msg) {
        synchronized (System.out) {
            System.out.println(msg);
        }
    }
}