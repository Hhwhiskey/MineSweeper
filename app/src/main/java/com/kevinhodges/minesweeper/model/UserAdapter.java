package com.kevinhodges.minesweeper.model;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.kevinhodges.minesweeper.R;
import com.kevinhodges.minesweeper.utils.Constants;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Kevin on 4/20/2016.
 */
public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {

    private static final String TAG = "UserAdapter";
    private static final String ALL_USER_LIST = "allUserList";
    private List mData;
    private Context mContext;
    private LayoutInflater mInflater;
    private AlertDialog.Builder mBuilder;
    private SharedPreferences mSharedPreferences;
    private SharedPreferences.Editor mEditor;

    public UserAdapter(Context context, List data) {
        this.mData = data;
        this.mInflater = LayoutInflater.from(context);
    }

    @Override
    public UserViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.custom_row_user, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(UserViewHolder viewHolder, int position) {
        viewHolder.update(position);
    }

    @Override
    public int getItemCount() {

        if (mData != null) {
            return mData.size();
        }
        return 0;
    }

    public class UserViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        TextView userName;
        TextView bestTime;
        TextView gamesWon;
        TextView gamesPlayed;
        TableLayout tl;

        public UserViewHolder(View itemView) {
            super(itemView);

            tl = (TableLayout) itemView.findViewById(R.id.tl_leaderboard);
            userName = (TextView) itemView.findViewById(R.id.cr_user_name);
            bestTime = (TextView) itemView.findViewById(R.id.cr_best_time);
            gamesWon = (TextView) itemView.findViewById(R.id.cr_games_won);
            gamesPlayed = (TextView) itemView.findViewById(R.id.cr_games_played);

        }

        public void update(int position) {

            User current = (User) mData.get(position);

            tl.setOnClickListener(this);
            tl.setOnLongClickListener(this);
            userName.setText(current.getName());

            int highScore = current.getHighScore();

            if (highScore == 0) {
                bestTime.setText("N/A");

            } else {

                bestTime.setText(String.valueOf(current.getHighScore()));
            }

            gamesWon.setText(String.valueOf(current.getGamesWon()));
            gamesPlayed.setText(String.valueOf(current.getGamesPlayed()));
        }

        @Override
        public void onClick(final View v) {
            if (v == tl) {
                mBuilder = new AlertDialog.Builder(v.getRootView().getContext(), R.style.MyAlertDialogStyle);
                mBuilder.setTitle("Switch User");
                mBuilder.setMessage("Are you sure you would like to switch to " + userName.getText().toString());
                mBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(v.getRootView().getContext());
                        mEditor = mSharedPreferences.edit();
                        mEditor.putString(Constants.CURRENT_USER, userName.getText().toString());
                        mEditor.commit();

                        Toast.makeText(v.getRootView().getContext(), "Profile changed to " + userName.getText().toString(), Toast.LENGTH_LONG).show();
                    }
                });

                mBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

                mBuilder.show();
            }
        }

        @Override
        public boolean onLongClick(View v) {
            deleteUser(v, getAdapterPosition());

            return false;
        }


        public void deleteUser(View v, final int position) {

            mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(v.getRootView().getContext());
            mEditor = mSharedPreferences.edit();

            mBuilder = new AlertDialog.Builder(v.getRootView().getContext(), R.style.MyAlertDialogStyle);
            mBuilder.setTitle("Delete User");
            mBuilder.setMessage("Are you sure you would like to delete " + userName.getText().toString() + "?");
            mBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    Gson gson = new Gson();
                    String json = mSharedPreferences.getString(ALL_USER_LIST, "");

                    User[] sharedPrefsUserList = gson.fromJson(json, User[].class);
                    List<User> allUserList = Arrays.asList(sharedPrefsUserList);
                    allUserList = new ArrayList<>(allUserList);

                    allUserList.remove(position);
                    String userListJson = gson.toJson(allUserList);

                    mEditor.putString(ALL_USER_LIST, userListJson);
                    mEditor.commit();

                    mData.remove(position);
                    notifyItemRemoved(position);

                }
            });

            mBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });

            mBuilder.show();
        }
    }
}
