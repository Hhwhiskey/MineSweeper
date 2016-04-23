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
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.kevinhodges.minesweeper.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Kevin on 4/20/2016.
 */
public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {

    private List mData;
    private Context mContext;
    private LayoutInflater mInflater;
    private AlertDialog.Builder builder;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

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
        LinearLayout customRow;

        public UserViewHolder(View itemView) {
            super(itemView);

            customRow = (LinearLayout) itemView.findViewById(R.id.layout_custom_row);
            userName = (TextView) itemView.findViewById(R.id.cr_user_name);
            bestTime = (TextView) itemView.findViewById(R.id.cr_best_time);
            gamesWon = (TextView) itemView.findViewById(R.id.cr_games_won);
            gamesPlayed = (TextView) itemView.findViewById(R.id.cr_games_played);

        }

        public void update(int position) {

            User current = (User) mData.get(position);

            customRow.setOnClickListener(this);
            customRow.setOnLongClickListener(this);
            userName.setText(current.getName());

            String bestDifficulty = current.getDifficulty();

            if (bestDifficulty.equals("0")) {
                bestTime.setText("No wins yet");
            } else {
                bestTime.setText(String.valueOf(current.getBestTime()) + " on " + bestDifficulty);
            }


            gamesWon.setText(String.valueOf(current.getGamesWon()) + " won");
            gamesPlayed.setText(String.valueOf(current.getGamesPlayed()) + " played");


        }

        @Override
        public void onClick(final View v) {
            if (v == customRow) {
                builder = new AlertDialog.Builder(v.getRootView().getContext());
                builder.setTitle("Switch User");
                builder.setMessage("Are you sure you would like to switch to " + userName.getText().toString());
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(v.getRootView().getContext());
                        editor = sharedPreferences.edit();
                        editor.putString("currentUser", userName.getText().toString());
                        editor.commit();

                        Toast.makeText(v.getRootView().getContext(), "Profile changed to " + userName.getText().toString(), Toast.LENGTH_LONG).show();
                    }
                });

                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

                builder.show();
            }
        }


        @Override
        public boolean onLongClick(View v) {
            deleteUser(v, getAdapterPosition());

            return false;
        }


        public void deleteUser(View v, final int position) {

            sharedPreferences = PreferenceManager.getDefaultSharedPreferences(v.getRootView().getContext());
            editor = sharedPreferences.edit();

            builder = new AlertDialog.Builder(v.getRootView().getContext());
            builder.setTitle("Delete User");
            builder.setMessage("Are you sure you would like to delete " + userName.getText().toString() + "?");
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
//                    String userToDelete = "user" + userName.getText().toString();
//                    String sharedPrefUserToDelete = sharedPreferences.getString("user" + userToDelete, "");
//                    sharedPreferences.edit().remove(sharedPrefUserToDelete).commit();\

                    Gson gson = new Gson();
                    String json = sharedPreferences.getString("allUserList", "");

                    User[] sharedPrefsUserList = gson.fromJson(json, User[].class);
                    List<User> allUserList = Arrays.asList(sharedPrefsUserList);
                    allUserList = new ArrayList<>(allUserList);

                    allUserList.remove(position);
                    String userListJson = gson.toJson(allUserList);

                    editor.putString("allUserList", userListJson);
                    editor.commit();

                    mData.remove(position);
                    notifyItemRemoved(position);

                }
            });

            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });

            builder.show();
        }
    }
}
