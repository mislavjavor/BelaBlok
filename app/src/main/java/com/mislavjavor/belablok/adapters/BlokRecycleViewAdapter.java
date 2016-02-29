package com.mislavjavor.belablok.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Toast;

import com.mislavjavor.belablok.MainActivity;
import com.mislavjavor.belablok.R;
import com.mislavjavor.belablok.models.SingleGameModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * Created by mislavjavor on 11/02/16.
 */
public class BlokRecycleViewAdapter extends RecyclerView.Adapter<BlokRecycleViewAdapter.BlokViewHolder>{

    private List<SingleGameModel> mGameModels;
    private Context mContext;
    private Stack<List<SingleGameModel>> mModelsBackStack  = new Stack<List<SingleGameModel>>();

    @Override
    public BlokViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.singe_game_item, parent, false);
        BlokViewHolder blokViewHolder = new BlokViewHolder(v);
        return blokViewHolder;
    }

    public void addModel(SingleGameModel item){
        mModelsBackStack.push(new ArrayList<SingleGameModel>(mGameModels));
        mGameModels.add(item);
        this.notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(BlokViewHolder holder, final int position) {
        holder.homeTeamScore.setText(Integer.toString(mGameModels.get(position).getHomeTeamScore()));
        holder.awayTeamScore.setText(Integer.toString(mGameModels.get(position).getAwayTeamScore()));
        final BlokRecycleViewAdapter self = this;
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(mContext);
                builder.setItems(new CharSequence[]{"Uredi", "Obriši"}, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which) {
                                    case 0:
                                        createEditDialog(position);
                                        break;
                                    case 1:
                                        mModelsBackStack.push(new ArrayList<SingleGameModel>(mGameModels));
                                        mGameModels.remove(position);
                                        self.notifyDataSetChanged();
                                        ((MainActivity) mContext).updateScores();
                                        break;
                                    default:
                                        break;
                                }
                            }
                        }).setNegativeButton("Odustani",null);
                android.support.v7.app.AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
    }

    public void undo(){
        if(!mModelsBackStack.empty()){
            mGameModels = mModelsBackStack.pop();
            ((MainActivity)mContext).updateScores();

            this.notifyDataSetChanged();
        }
    }

    public void update(SingleGameModel model, int index){
        mGameModels.set(index, model);
        ((MainActivity)mContext).updateScores();
        this.notifyDataSetChanged();
    }

    public int calculateHomeScore(){
        int homeScore = 0;
        for(SingleGameModel item : mGameModels){
            homeScore += item.getHomeTeamScore();
        }
        return homeScore;
    }

    public int calculateAwayScore(){
        int awayScore = 0;
        for(SingleGameModel item : mGameModels){
            awayScore += item.getAwayTeamScore();
        }
        return awayScore;
    }

    public void clear(){
        mModelsBackStack.push(new ArrayList<SingleGameModel>(mGameModels));
        mGameModels.clear();
        this.notifyDataSetChanged();

        ((MainActivity)mContext).updateScores();
    }

    public void createEditDialog(int itemClickedPosition){
        ((MainActivity)mContext).handleFabClick(true, itemClickedPosition);
    }

    @Override
    public int getItemCount() {
        return mGameModels.size();
    }

    public BlokRecycleViewAdapter(List<SingleGameModel> gameModels, Context context){
        mGameModels = gameModels;
        mContext = context;
    }


    public static class BlokViewHolder extends RecyclerView.ViewHolder{

        TextView homeTeamScore;
        TextView awayTeamScore;

        private Context mContext;

        public BlokViewHolder(View itemView) {
            super(itemView);
            homeTeamScore = (TextView) itemView.findViewById(R.id.single_game_home_team_score);
            awayTeamScore = (TextView) itemView.findViewById(R.id.single_game_away_team_score);
        }
    }
}
