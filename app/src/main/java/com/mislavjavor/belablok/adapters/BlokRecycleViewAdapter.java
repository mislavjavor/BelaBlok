package com.mislavjavor.belablok.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mislavjavor.belablok.R;
import com.mislavjavor.belablok.models.SingleGameModel;

import java.util.List;

/**
 * Created by mislavjavor on 11/02/16.
 */
public class BlokRecycleViewAdapter extends RecyclerView.Adapter<BlokRecycleViewAdapter.BlokViewHolder> {

    private List<SingleGameModel> mGameModels;

    @Override
    public BlokViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.singe_game_item, parent, false);
        BlokViewHolder blokViewHolder = new BlokViewHolder(v);
        return blokViewHolder;
    }

    @Override
    public void onBindViewHolder(BlokViewHolder holder, int position) {
        holder.homeTeamScore.setText(Integer.toString(mGameModels.get(position).getHomeTeamScore()));
        holder.awayTeamScore.setText(Integer.toString(mGameModels.get(position).getAwayTeamScore()));
    }

    @Override
    public int getItemCount() {
        return mGameModels.size();
    }

    public BlokRecycleViewAdapter(List<SingleGameModel> gameModels){
        mGameModels = gameModels;
    }

    public static class BlokViewHolder extends RecyclerView.ViewHolder{

        TextView homeTeamScore;
        TextView awayTeamScore;

        public BlokViewHolder(View itemView) {
            super(itemView);
            homeTeamScore = (TextView) itemView.findViewById(R.id.single_game_home_team_score);
            awayTeamScore = (TextView) itemView.findViewById(R.id.single_game_away_team_score);
        }
    }
}
