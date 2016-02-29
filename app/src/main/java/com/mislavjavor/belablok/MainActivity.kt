package com.mislavjavor.belablok

import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.Snackbar
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.View
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.text.TextWatcher
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import android.widget.TextView

import com.couchbase.lite.Database
import com.couchbase.lite.Document
import com.couchbase.lite.Manager
import com.couchbase.lite.android.AndroidContext
import com.mislavjavor.belablok.adapters.BlokRecycleViewAdapter
import com.mislavjavor.belablok.models.SingleGameModel

import java.lang.annotation.Documented
import java.util.ArrayList

import butterknife.Bind
import butterknife.ButterKnife
import com.pawegio.kandroid.find
import com.pawegio.kandroid.textWatcher


class MainActivity : AppCompatActivity() {

    internal val TAG = "CouchbaseEvents"

    var mBlokRecyclerView: RecyclerView? = null

    private var _models = ArrayList<SingleGameModel>()
    var models: ArrayList<SingleGameModel>
        get() = _models
        set(value) {
            _models = value
        }

    var adapter : BlokRecycleViewAdapter? = null

    var homeTotalScore : TextView? = null
    var awayTotalScore : TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        mBlokRecyclerView = findViewById(R.id.blok_recycler_view) as RecyclerView

        val toolbar = findViewById(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)

        supportActionBar?.setIcon(R.drawable.toolbar_icon_nat)
        supportActionBar?.title = ""

        val layoutManager = LinearLayoutManager(this)
        layoutManager.orientation = LinearLayoutManager.VERTICAL;

        mBlokRecyclerView?.layoutManager = layoutManager
        adapter = BlokRecycleViewAdapter(models, this)
        mBlokRecyclerView?.adapter = adapter

        homeTotalScore = findViewById(R.id.home_team_total_score_score) as TextView
        awayTotalScore = findViewById(R.id.away_team_total_score_score) as TextView

        val fab = findViewById(R.id.fab) as FloatingActionButton
        fab.setOnClickListener { view ->
            handleFabClick()
        }


    }

    var gameExtension = 0

    var isSettingHome = false
    var isSettingAway = false

    fun handleFabClick(shouldUpdate : Boolean = false, updateIndex : Int = 0){
        val builder = AlertDialog.Builder(this)
        builder.setView(R.layout.input_result_modal)

        val dialog = builder.create()
        dialog.show()

        val gameExt = dialog.findViewById(R.id.game_extended_et) as EditText
        val homeTeamScoreEt = dialog.findViewById(R.id.homeTeamScore) as EditText
        val awayTeamScore = dialog.findViewById(R.id.awayTeamScore) as EditText

        val gameBase = 162

        gameExtension = 0
        gameExt.textWatcher { 
            onTextChanged { text, start, count, after ->
                try {
                    gameExtension = text.toString().toInt()
                    homeTeamScoreEt.setText("")
                    awayTeamScore.setText("")
                } catch (e : Exception){
                    Log.d("number parse", e.toString())
                }

            }
        }
        homeTeamScoreEt.textWatcher {
            onTextChanged { text, start, count, after ->
                isSettingHome = true
                if(!isSettingAway){
                    try{
                        var homeValue = text.toString().toInt()
                        var awayValue = gameBase + gameExtension - homeValue
                        awayValue = if(awayValue < 0 ) -awayValue else awayValue

                        if(homeValue > (gameBase + gameExtension)){
                            homeTeamScoreEt.setText((gameBase + gameExtension).toString())
                        }else {
                            awayTeamScore.setText(awayValue.toString())
                        }
                    } catch(e: Exception){

                    } finally {
                        isSettingHome = false
                    }
                }
                isSettingHome = false
            }
        }
        awayTeamScore.textWatcher {
            onTextChanged { text, start, count, after ->
                isSettingAway = true
                if(!isSettingHome){
                    try{
                        var awayValue = text.toString().toInt()
                        var homeValue = gameBase + gameExtension - awayValue
                        homeValue = if(homeValue < 0) -homeValue else homeValue

                        if(awayValue > (gameBase + gameExtension)){
                            awayTeamScore.setText((gameBase + gameExtension).toString())
                        } else {
                            homeTeamScoreEt.setText(homeValue.toString())
                        }
                    } catch(e: Exception){

                    } finally {
                        isSettingAway = false
                    }
                }
                isSettingAway = false
            }
        }
        var enterScoreButton = dialog.findViewById(R.id.enterScoreButton) as android.support.v7.widget.AppCompatButton

        var cancelScoreButton = dialog.findViewById(R.id.cancel_score_button) as android.support.v7.widget.AppCompatButton

        enterScoreButton.setOnClickListener { view ->
            val model = SingleGameModel()
            try{
                model.awayTeamScore = awayTeamScore.text.toString().toInt()
                model.homeTeamScore = homeTeamScoreEt.text.toString().toInt()
                if(shouldUpdate){
                    adapter?.update(model, updateIndex)
                } else {
                    adapter?.addModel(model)
                }
                dialog.dismiss()
                updateScores()
            } catch(e: Exception){
                dialog.dismiss()
            }
        }

        cancelScoreButton.setOnClickListener { view ->
            dialog.dismiss();
        }

    }

    fun updateScores(){
        homeTotalScore?.text = adapter?.calculateHomeScore().toString()
        awayTotalScore?.text = adapter?.calculateAwayScore().toString()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if(item?.itemId == R.id.action_undo){
            adapter?.undo();
        } else if(item?.itemId == R.id.action_clear){
            adapter?.clear();
        }
        return super.onOptionsItemSelected(item)
    }
}
