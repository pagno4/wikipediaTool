package app;

import app.remoteservices.RemoteServices;
import app.remoteservices.ReturnMessage;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

public class RequestClient {
    private static RequestClient singleton;

    public static RequestClient instance() {
        if (singleton == null)
            singleton = new RequestClient();
        return singleton;
    }

    private RequestClient() {
    }

    /**
     * HTTP POST for register user name.
     *
     * @param name   User name.
     * @param action Lambda-function.
     */
    public void addPlayer(String name, Consumer<String> action) {
        Call<ReturnMessage> add = RemoteServices.getInstance().getPlayersService().addPlayer(name);
        add.enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<ReturnMessage> call, Response<ReturnMessage> response) {
                if (response.isSuccessful() && response.body() != null) {
                    action.accept(response.body().getMessage());
                }
            }

            @Override
            public void onFailure(Call<ReturnMessage> call, Throwable t) {
                log(t.getMessage());
            }
        });
    }

    /**
     * HTTP GET for extract list user.
     *
     * @param action Lambda-function.
     */
    public void allUsers(Consumer<List<String>> action) {
        Call<List<String>> res = RemoteServices.getInstance().getPlayersService().allPlayers();
        res.enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<List<String>> call, Response<List<String>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    action.accept(response.body());
                }
            }

            @Override
            public void onFailure(Call<List<String>> call, Throwable t) {
                log(t.getMessage());
            }
        });
    }

    /**
     * HTTP DELETE for delete user from server.
     *
     * @param name User name.
     */
    public void deleteUser(String name) {
        Call<ReturnMessage> call = RemoteServices.getInstance().getPlayersService().deletePlayer(name);
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<ReturnMessage> call, Response<ReturnMessage> response) {
                if (response.isSuccessful() && response.body() != null) {
                    log(response.body().getMessage());
                }
            }

            @Override
            public void onFailure(Call<ReturnMessage> call, Throwable t) {
                log(t.getMessage());
            }
        });
    }

    /**
     * HTTP PUT for take a boxe.
     *
     * @param name   User name.
     * @param button Tile button.
     */
    public void takeBox(String name, TileButton button, Consumer<Boolean> consumer) {
        Call<ReturnMessage> call = RemoteServices.getInstance().getPuzzleService().take(name, button.getTile().getOriginalPosition());
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<ReturnMessage> call, Response<ReturnMessage> response) {
                if (response.isSuccessful() && response.body() != null) {
                    button.setBorder(BorderFactory.createLineBorder(Color.red));
                    button.setColor(Color.red);
                    button.getTile().setTaker(name);
                    consumer.accept(response.body().getResult());
                }
            }

            @Override
            public void onFailure(Call<ReturnMessage> call, Throwable t) {
                log(t.getMessage());
            }
        });
    }

    /**
     * HTTP PUT for release a boxe.
     *
     * @param name   Name user.
     * @param button Tile button.
     */
    public void releaseBox(String name, TileButton button) {
        Call<ReturnMessage> call = RemoteServices.getInstance().getPuzzleService().release(name, button.getTile().getOriginalPosition());
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<ReturnMessage> call, Response<ReturnMessage> response) {
                if (response.isSuccessful() && response.body() != null) {
                    button.setBorder(BorderFactory.createLineBorder(Color.gray));
                    button.setColor(Color.gray);
                }
            }

            @Override
            public void onFailure(Call<ReturnMessage> call, Throwable t) {
                log(t.getMessage());
            }
        });
    }

    /**
     * HTTP GET for get list tile.
     *
     * @param action Lambda-function
     */
    public void mappingBox(Consumer<Set<app.remoteservices.Box>> action) {
        Call<Set<app.remoteservices.Box>> call = RemoteServices.getInstance().getPuzzleService().getMappings();
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<Set<app.remoteservices.Box>> call, Response<Set<app.remoteservices.Box>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    action.accept(response.body());
                }
            }

            @Override
            public void onFailure(Call<Set<app.remoteservices.Box>> call, Throwable t) {
                log(t.getMessage());
            }
        });
    }

    /**
     * HTTP PUT for move the box.
     *
     * @param name   Name user.
     * @param id     Initial position.
     * @param id2    Final position.
     * @param action Lambda-function.
     */
    public void moveBox(String name, Integer id, Integer id2, Consumer<Boolean> action) {
        Call<ReturnMessage> call = RemoteServices.getInstance().getPuzzleService().move(name, id, id2);
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<ReturnMessage> call, Response<ReturnMessage> response) {
                if (response.isSuccessful() && response.body() != null) {
                    action.accept(response.body().getResult());
                }
            }

            @Override
            public void onFailure(Call<ReturnMessage> call, Throwable t) {
                log(t.getMessage());
            }
        });
    }

    /**
     * HTTP PUT for set position default.
     */
    public void gameReset(Consumer<Boolean> action) {
        Call<ReturnMessage> call = RemoteServices.getInstance().getPuzzleService().gameReset();
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<ReturnMessage> call, Response<ReturnMessage> response) {
                action.accept(true);
            }

            @Override
            public void onFailure(Call<ReturnMessage> call, Throwable t) {
                action.accept(true);
            }
        });
    }

    private void log(String msg) {
        synchronized (System.out) {
            System.out.println("[Info] " + msg);
        }
    }
}