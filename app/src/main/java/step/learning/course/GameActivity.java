package step.learning.course;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.Toast;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GameActivity extends AppCompatActivity {

    private final int N = 4;
    private final int[][] cells = new int[N][N];
    private final int[][] savesCells = new int[N][N];
    private final int[][] canMoveCells = new int[N][N];
    private final TextView[][] tvCells = new TextView[N][N];
    private final Random random = new Random();
    private final String BEST_SCORE_FILENAME = "best_score.txt";
    private int emptyCell = 0;
    private int cntSpawnCell = 1;

    private Animation spawnAnimation;
    private Animation colapseAnimation;

    private boolean continuePlay;
    private int score = 0;
    private int bestScore = 0;
    private TextView tvScore;
    private TextView tvBestScore;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        tvScore = findViewById(R.id.game_tv_score);
        tvBestScore = findViewById(R.id.game_tv_best_score);
        tvScore.setText( getString(R.string.game_score));
        tvBestScore.setText( getString(R.string.game_best_score));

        spawnAnimation = AnimationUtils.loadAnimation(this, R.anim.cell_spawn);
        spawnAnimation.reset();
        colapseAnimation = AnimationUtils.loadAnimation(this, R.anim.cell_colapse);
        colapseAnimation.reset();

        for (int i = 0 ; i <N; i++){
            for (int j = 0 ; j<N;j++){
                tvCells[i][j] = findViewById(
                        getResources().getIdentifier(
                                "game_cell_"+ i + j,
                                "id",
                                getPackageName()
                        )
                );
            }
        }

        findViewById(R.id.game_field).setOnTouchListener(
                new OnSwipeTouchListener( this){
                    @Override
                    public void onSwipeRight() {
                        if( canMoveRight() ) {
                            saveField() ;
                            moveRight() ;
                            spawnCell() ;
                            showField() ;
                        }
                        else {
                            Toast.makeText(
                                            GameActivity.this,
                                            "No Right Move",
                                            Toast.LENGTH_SHORT)
                                    .show();
                        }
                    }
                    @Override
                    public void onSwipeLeft() {
                        if( canMoveLeft() ) {
                            saveField() ;
                            moveLeft() ;
                            spawnCell() ;
                            showField() ;
                        }
                        else {
                            Toast.makeText(
                                            GameActivity.this,
                                            "No Left Move",
                                            Toast.LENGTH_SHORT)
                                    .show();
                        }
                    }
                    @Override
                    public void onSwipeTop() {
                        if( canMoveTop() ) {
                            saveField() ;
                            moveTop(); ;
                            spawnCell() ;
                            showField() ;
                        }
                        else {
                            Toast.makeText(
                                            GameActivity.this,
                                            "No Top Move",
                                            Toast.LENGTH_SHORT)
                                    .show();
                        }
                    }@Override
                    public void onSwipeBottom() {
                        if( canMoveBottom() ) {
                            saveField() ;
                            moveBottom(); ;
                            spawnCell() ;
                            showField() ;
                        }
                        else {
                            Toast.makeText(
                                            GameActivity.this,
                                            "No Bottom Move",
                                            Toast.LENGTH_SHORT)
                                    .show();
                        }
                    }
                }
        );
        newGame(null);
    }

    private void newGame(View view){
        for (int i = 0; i <N;i++){
            for (int j = 0; j <N; j++){
                cells[i][j] = emptyCell;
            }
        }
        continuePlay = false;
        score = 0;
        loadBestScore();
        spawnCell();
        spawnCell();
        saveField();
        showField();
    }
    private boolean canMoveRight(){
        for( int i = 0; i < N; i++ ) {
            for( int j = 0; j < N-1; j++ ) {
                if( cells[ i ][ j ] != 0 && cells[ i ][ j + 1 ] == 0
                        || cells[ i ][ j ] != 0 && cells[ i ][ j ] == cells[ i ][ j + 1 ] ) {
                    return true ;
                }
            }
        }
        return false;
    }
    private boolean canMoveLeft() {
        for( int i = 0; i < N; i++ ) {
            for( int j = 0; j < N-1; j++ ) {
                if( cells[ i ][ j ] == 0 && cells[ i ][ j + 1 ] != 0
                        || cells[ i ][ j ] != 0 && cells[ i ][ j ] == cells[ i ][ j + 1 ] ) {
                    return true ;
                }
            }
        }
        return false ;
    }
    private boolean canMoveBottom(){
        for( int i = 0; i < N; i++ ) {
            for( int j = 0; j < N-1; j++ ) {
                if( cells[ i ][ j ] != 0 && cells[ i + 1][ j ] == 0
                        || cells[ i ][ j ] != 0 &&
                        cells[ i ][ j ] == cells[ i + 1 ][ j ] ) {
                    return true ;
                }
            }
        }
        return false;
    }
    private boolean canMoveTop() {
        for( int i = 0; i < N; i++ ) {
            for( int j = 0; j < N-1; j++ ) {
                if( cells[ i ][ j ] == 0 &&
                        cells[ i  + 1 ][ j] != 0
                        || cells[ i ][ j ] != 0 &&
                        cells[ i ][ j ] == cells[ i  + 1 ][ j] ) {
                    return true ;
                }
            }
        }
        return false ;
    }
    private void showField(){
        Resources resources = getResources();
        String packageName = getPackageName();
        for (int i = 0; i < N;i++){
            for (int j=0 ; j <N;j++){
                tvCells[i][j].setText(String.valueOf(cells[i][j]));
                tvCells[i][j].setTextAppearance(resources.getIdentifier(
                            "GameCell_" + cells[i][j],
                            "style",
                            packageName
                        )
                );
                tvCells[i][j].setBackgroundColor(
                        resources.getColor(
                                resources.getIdentifier(
                                        "game_bg_"+ cells[i][j],
                                        "color",
                                        packageName
                                ),
                                getTheme()
                        )
                );
            }
        }
        tvScore.setText(getString(R.string.game_score,String.valueOf(score)));
        if (score > bestScore){
            bestScore = score;
            saveBestScore();
            tvBestScore.setText(getString(R.string.game_best_score,String.valueOf(bestScore)));
        }
        if (score >= 8 && !continuePlay) {
            showWinMessage();
        }
    }
    private void moveRight(){
        boolean wasReplace;
        for(int i = 0; i < N; i++) {
            do {
                wasReplace = false;
                for (int j = N - 1; j > 0; j--) {
                    if (cells[i][j] == 0 && cells[i][j - 1] != 0) {
                        cells[i][j] = cells[i][j - 1];
                        cells[i][j - 1] = 0;
                        wasReplace = true;
                    }
                }
            } while (wasReplace);
            for (int j = N - 1; j > 0; j--) {
                if (cells[i][j] == cells[i][j - 1] && cells[i][j] != emptyCell) {
                    score = score + cells[i][j] + cells[i][j-1];
                    cells[i][j] = cells[i][j] * -2;//костыль
                    cells[i][j - 1] = emptyCell;

                }
            }
            for (int j = N - 1; j > 0; j--) {
                if (cells[i][j] == emptyCell && cells[i][j-1] != emptyCell) {
                    cells[i][j] = cells[i][j - 1];
                    cells[i][j - 1] = emptyCell;
                }
            }

            // сам костыль
            for (int j = N - 1; j > 0; j--) {
                if (cells[i][j] < emptyCell) {
                    cells[i][j] = -cells[i][j];
                    tvCells[i][j].startAnimation(colapseAnimation);
                }
            }
        }

    }
    private void moveBottom(){
        boolean wasReplace;
        for(int i = N - 1; i > 0; i--) {
            do {
                wasReplace = false;
                for (int j = N - 1; j > 0; j--) {
                    if (cells[i][j] == 0 && cells[i-1][j] != 0) {
                        cells[i][j] = cells[i-1][j];
                        cells[i-1][j] = 0;
                        wasReplace = true;
                    }
                }
            } while (wasReplace);
            for (int j = N - 1; j > 0; j--) {
                if (cells[i][j] == cells[i - 1][j] && cells[i][j] != emptyCell) {
                    score = score + cells[i][j] + cells[i - 1][j];
                    cells[i][j] = cells[i][j] * -2;
                    cells[i - 1][j] = emptyCell;

                }
            }
            for (int j = N - 1; j > 0; j--) {
                if (cells[i][j] == emptyCell && cells[i-1][j] != emptyCell) {
                    cells[i][j] = cells[i-1][j ];
                    cells[i- 1][j ] = emptyCell;
                }
            }

            // сам костыль
            for (int j = N - 1; j > 0; j--) {
                if (cells[i][j] < emptyCell) {
                    cells[i][j] = -cells[i][j];
                    tvCells[i][j].startAnimation(colapseAnimation);
                }
            }
        }
    }
    private void moveLeft(){
        for( int i = 0; i < N; i++ ) {
            int k = -1 ;
            for( int j = 0; j < N; j++ ) {
                if( cells[ i ][ j ] != 0 ) {
                    if( k == -1 || cells[ i ][ j ] != cells[ i ][ k ] ) {
                        k = j ;
                    }
                    else {
                        cells[ i ][ k ] += cells[ i ][ j ] ;
                        score += cells[ i ][ k ] ;
                        cells[ i ][ j ] = 0 ;
                        k = -1;
                    }
                }
            }
            // move
            k = 0;
            for( int j = 0; j < N; j++ ) {
                if( cells[ i ][ j ] != 0 ) {
                    cells[ i ][ k ] = cells[ i ][ j ] ;
                    if( j != k ) cells[ i ][ j ] = 0 ;
                    ++k;
                }
            }
        }
    }
    private void moveTop(){
        boolean wasReplace;
        for(int i = 0; i < N - 1; i++) {
            do {
                wasReplace = false;
                for (int j = N - 1; j > 0; j--) {
                    if (cells[i][j] == 0 && cells[i+1][j] != 0) {
                        cells[i][j] = cells[i+1][j];
                        cells[i+1][j] = 0;
                        wasReplace = true;
                    }
                }
            } while (wasReplace);
            for (int j = N - 1; j > 0; j--) {
                if (cells[i][j] == cells[i + 1][j] && cells[i][j] != emptyCell) {
                    score = score + cells[i][j] + cells[i + 1][j];
                    cells[i][j] = cells[i][j] * -2;
                    cells[i + 1][j] = emptyCell;

                }
            }
            for (int j = N - 1; j > 0; j--) {
                if (cells[i][j] == emptyCell && cells[i-1][j] != emptyCell) {
                    cells[i][j] = cells[i+1][j ];
                    cells[i+ 1][j ] = emptyCell;
                }
            }

            // сам костыль
            for (int j = N - 1; j > 0; j--) {
                if (cells[i][j] < emptyCell) {
                    cells[i][j] = -cells[i][j];
                    tvCells[i][j].startAnimation(colapseAnimation);
                }
            }
        }
    }
    private void saveBestScore(){
        try (
            FileOutputStream fileStream = openFileOutput(BEST_SCORE_FILENAME, Context.MODE_PRIVATE);
            DataOutputStream writer = new DataOutputStream(fileStream)
        ){
            writer.writeInt(bestScore);
            writer.flush();
        } catch (IOException ex) {
            Log.d("saveBestScore", ex.getMessage());
        }
    }
    private void loadBestScore(){
        try (FileInputStream fileInputStream = openFileInput(BEST_SCORE_FILENAME);
             DataInputStream reader = new DataInputStream(fileInputStream)
        ){
            bestScore = reader.readInt();
        }catch (IOException ex) {
            Log.d("loadBestScore", ex.getMessage());
            bestScore = 0;
        }
    }
    private boolean spawnCell(){
        List<Coord> coordinates = new ArrayList<>();
        for (int i = 0; i <N;i++){
            for (int j = 0; j <N; j++){
                if (cells[i][j] == emptyCell){
                    coordinates.add(new Coord(i , j));
                }
            }
        }
        int cnt = coordinates.size();
        if (cnt < cntSpawnCell // поставити колво запроса замість 0
        ){
            return false;
        }
        for (int c = 0; c < cntSpawnCell; c++
                //від 0 до колва запроса
        ) {
            int randIndex = random.nextInt(cnt);
            int x = coordinates.get(randIndex).getX();
            int y = coordinates.get(randIndex).getY();
            cells[x][y] = random.nextInt(10) == 0 ? 4 : 2;
            tvCells[x][y].startAnimation(spawnAnimation);
        }
        return true;
    }
    private void saveField(){
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                savesCells[i][j] = cells[i][j];
            }
        }
    }
    private void undoMove(){
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                cells[i][j] = savesCells[i][j];
            }
        }
    }
    private void showWinMessage(){
        new AlertDialog.Builder(this, androidx.appcompat.R.style.Base_Theme_AppCompat)
                .setTitle(R.string.game_win_dialog_title)
                .setMessage(R.string.game_win_dialog_message)
                .setIcon(android.R.drawable.star_on)
                .setCancelable(false)
                .setPositiveButton(R.string.game_yes_dialog_btn, (dialog, button) -> {
                    continuePlay = true;
                } )
                .setNegativeButton(R.string.game_exit_dialog_btn, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int button) {
                        finish();
                    }
                })
                .setNeutralButton(R.string.game_new_dialog_btn,(dialog, button) -> {
                    newGame(null);
                } )
                .show();
    }
    private class Coord{
        private int x;
        private int y;

        public Coord(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public int getX() {
            return x;
        }

        public int getY() {
            return y;
        }
    }

}