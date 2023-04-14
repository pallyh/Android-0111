package step.learning.course;

import androidx.appcompat.app.AppCompatActivity;

import android.content.res.Resources;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GameActivity extends AppCompatActivity {

    private final int N = 4;
    private final int[][] cells = new int[N][N];
    private final TextView[][] tvCells = new TextView[N][N];
    private final Random random = new Random();
    private int cntSpawnCell = 1;

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
                            Toast.makeText( GameActivity.this,
                                    "Right",
                                    Toast.LENGTH_SHORT)
                                    .show();
                    }
                    @Override
                    public void onSwipeLeft() {
                            Toast.makeText( GameActivity.this,
                                    "Left",
                                    Toast.LENGTH_SHORT)
                                    .show();
                    }
                    @Override
                    public void onSwipeTop() {
                        /*for (int i = 0; i <N;i++){
                            for (int j = 0; j < N; j++){
                                if (cells[i-1][j] == 0){
                                    cells[i-1][j] = cells[i][j];
                                    cells[i][j] = 0;
                                }
                            }
                        }
                        spawnCell();
                        showField();*/
                         Toast.makeText( GameActivity.this,
                                    "Top",
                                    Toast.LENGTH_SHORT)
                                    .show();
                    }@Override
                    public void onSwipeBottom() {
                            Toast.makeText( GameActivity.this,
                                    "Bottom",
                                    Toast.LENGTH_SHORT)
                                    .show();
                    }
                }
        );
        newGame();
    }

    private void newGame(){
        for (int i = 0; i <N;i++){
            for (int j = 0; j <N; j++){
                cells[i][j] = 0;
            }
        }
        score = 0;
        spawnCell();
        spawnCell();
        showField();
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
    }

    private boolean spawnCell(){
        List<Coord> coordinates = new ArrayList<>();
        for (int i = 0; i <N;i++){
            for (int j = 0; j <N; j++){
                if (cells[i][j] == 0){
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
        }
        return true;
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