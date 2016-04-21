package com.kevinhodges.minesweeper.model;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.kevinhodges.minesweeper.R;

import java.util.List;

/**
 * Created by Kevin on 4/20/2016.
 */
public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {

    private List mData;
    private Context mContext;
    private LayoutInflater mInflater;

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

    public class UserViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView userName;
        TextView bestTime;
        TextView gamesWon;
        TextView gamesPlayed;

        public UserViewHolder(View itemView) {
            super(itemView);

            userName = (TextView) itemView.findViewById(R.id.cr_user_name);
            bestTime = (TextView) itemView.findViewById(R.id.cr_best_time);
            gamesWon = (TextView) itemView.findViewById(R.id.cr_games_won);
            gamesPlayed = (TextView) itemView.findViewById(R.id.cr_games_played);

        }

        public void update(int position) {

            User current = (User) mData.get(position);

            userName.setText(current.getName());
            bestTime.setText(String.valueOf(current.getBestTime()) + " seconds");
            gamesWon.setText(String.valueOf(current.getGamesWon()) + " won");
            gamesPlayed.setText(String.valueOf(current.getGamesPlayed()) + " played");

        }

        @Override
        public void onClick(View v) {

        }
    }
}
