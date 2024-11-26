package com.example.ballsortinggame;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.gridlayout.widget.GridLayout;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class HardGame extends AppCompatActivity {

    private final String[] colors = {"#FF0000", "#008000", "#0000FF"};
    private final String emptyColor = "#03A9F4";

    private GridLayout gridLayout;
    private Button selectedButton;
    private int selectedColor;
    private boolean buttonSelected = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_hard_game);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        gridLayout = findViewById(R.id.hardGrid);
        Button mainMenu = findViewById(R.id.menuBtn);

        // list to hold the button colors
        ArrayList<String> buttonColors = new ArrayList<String>();

        // add four of each color to the list
        for (int i = 0; i < 4; i++) {
            buttonColors.addAll(Arrays.asList(colors));
        }

        // shuffle the colors
        Collections.shuffle(buttonColors);

        // assign colors to each button and add on click listeners
        assignColorsAndSetListeners(buttonColors);

        mainMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                returnToMainMenu();
            }
        });
    }

    /**
     * Assign colors to the buttons and set up on click listeners
     * @param buttonColors List of colors to assign to buttons
     */
    private void assignColorsAndSetListeners(ArrayList<String> buttonColors) {
        for (int row = 1; row < 5; row++) {
            for (int col = 0; col < 4; col++) {
                // calculate the index for the button
                Button button = getButton(row, col);

                if (col < 3) {
                    // set the colors for the non-empty elements
                    button.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor(buttonColors.remove(0))));
                } else {
                    // set the colors for the empty elements
                    button.setBackgroundTintList(ColorStateList.valueOf(getEmptyColor()));
                }
                int finalCol = col;

                // set an onClickListener for each button element
                button.setOnClickListener(v -> handleButtonClick(finalCol));
            }
        }
    }

    /**
     * Handles the button click events, either selecting or places the elements
     * @param col The column index where the click occurred
     */
    private void handleButtonClick(int col) {
        if (!buttonSelected) {
            // check if column contains non-empty element
            int row = checkColorsInColumn(col);

            if (row != -1) {
                selectNonEmptyElement(col, row);

                if (!checkIfValidMoves()) {
                    showLoseDialog();
                }
            }
        } else {
            // get the lowest empty element
            int emptyRow = findEmptyRowInColumn(col);

            if (isMoveValid(col) && emptyRow != -1) {
                placeSelectedElement(col, emptyRow);

                // check win condition after each element placement
                if (checkWinCondition()) {
                    showWinDialog();
                }
            } else {
                Toast.makeText(this, "Invalid move", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * Shows to game over dialog
     */
    private void showLoseDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Game Over!")
                .setMessage("There are no more valid moves.\nWould you like to play again?")
                .setPositiveButton("Yes", (dialog, which) -> restartGame())
                .setNegativeButton("Main Menu", (dialog, which) -> returnToMainMenu())
                .setCancelable(false)
                .show();
    }

    /**
     * Iterates over the columns to check if there are any valid moves left
     * @return True if there is a valid move, otherwise false
     */
    private boolean checkIfValidMoves() {
        for (int col = 0; col < 4; col++) {
            // check if there is a non-empty element in the column
            int nonEmptyRow = checkColorsInColumn(col);

            // if row is not empty
            if (nonEmptyRow != -1) {
                // loop through other columns
                for (int targetCol = 0; targetCol < 4; targetCol++) {
                    // skip the selected column
                    if (targetCol != col) {
                        // check for empty row in target column
                        int emptyRow = findEmptyRowInColumn(targetCol);

                        // check if the top element matches the selected element color
                        if (isMoveValid(targetCol) && emptyRow != -1) {
                            return true;
                        }
                    }
                }
            }
        }
        return false; // No valid moves found
    }


    /**
     * Places the selected button element into the clicked column
     * @param col Index of column where element will be places
     * @param emptyRow Index of the lowest empty row
     */
    private void placeSelectedElement(int col, int emptyRow) {
        // place the selected color in the lowest empty row
        Button targetButton = (Button) gridLayout.getChildAt((emptyRow * 4) + col);

        // set the color of the placed element
        targetButton.setBackgroundTintList(ColorStateList.valueOf(selectedColor));

        // reset the selected button in row 0 to background color
        String backgroundColor = "#03A9F4";
        selectedButton.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor(backgroundColor)));

        selectedButton.setText("");

        // reset
        selectedButton = null;
        buttonSelected = false;
    }

    /**
     * Selects the next element in the column to move
     * @param col The index of the selected column
     * @param row The row index of the next element to move
     */
    private void selectNonEmptyElement(int col, int row) {
        // the button element to move
        Button button = getButton(row, col);

        // get the color of the selected button element
        selectedColor = button.getBackgroundTintList().getDefaultColor();

        // change the original spots color with empty element
        button.setBackgroundTintList(ColorStateList.valueOf(getEmptyColor()));

        // get the top element in the column (row 0)
        Button topButton = (Button) gridLayout.getChildAt(col);

        // set the selection row (row 0) element to the selected color
        topButton.setBackgroundTintList(ColorStateList.valueOf(selectedColor));

        topButton.setText("?");

        selectedButton = topButton;
        buttonSelected = true;
    }

    /**
     * Checks for the first non-empty element in the selected column
     * @param col Index of the selected column
     * @return Returns the row index of next element or -1 if there is none
     */
    private int checkColorsInColumn(int col) {
        // loop through each row of the selected column starting at row 2 (index 1)
        for (int row = 1; row < 5; row++) {
            Button button = getButton(row, col);

            // get the color of the current button element
            ColorStateList buttonColor = button.getBackgroundTintList();
            if (buttonColor != null) {
                int color = buttonColor.getDefaultColor();

                // Check if the color is not empty
                if (color != getEmptyColor()) {
                    // return row number of first non-empty button element
                    return row;
                }
            }
        }
        // return -1 if no non-empty button element is found
        return -1;
    }

    /**
     * Finds the lowest empty row to place the moved element
     * @param col Index of selected column
     * @return Returns the index of the lowest row or -1 if none available
     */
    private int findEmptyRowInColumn(int col) {
        // find the lowest empty button element in column
        for (int row = 4; row >= 1; row--) {
            Button button = getButton(row, col);

            // get color of current button element
            ColorStateList buttonColor = button.getBackgroundTintList();

            // check if the current element color is empty
            if (buttonColor != null && buttonColor.equals(ColorStateList.valueOf(getEmptyColor()))) {
                // return the lowest empty element in column
                return row;
            }
        }
        return -1; // No empty row available
    }

    /**
     * Checks if the move is valid - compares the top element color to selected element
     * @param col Index of column selected to move to
     * @return Returns true if move is valid, otherwise false
     */
    private boolean isMoveValid(int col) {
        int row = checkColorsInColumn(col);

        if (row == -1) {
            // return true if column is empty
            return true;
        }

        Button button = getButton(row, col);

        // get the top button's color in the column
        int topColor = button.getBackgroundTintList().getDefaultColor();

        // compare colors and return result
        return topColor == selectedColor;
    }

    /**
     * Helper function to get the integer value of the empty color
     * @return Returns integer value of the background (empty color)
     */
    private int getEmptyColor() {
        return Color.parseColor(emptyColor);
    }

    /**
     * Used to get the button at a certain row and column
     * @param row Index of row
     * @param col Index of button
     * @return Returns the button at the provided index
     */
    private Button getButton(int row, int col) {
        return (Button) gridLayout.getChildAt((row * 4) + col);
    }

    /**
     * Checks if all the columns have only one color each
     * @return Returns true if win conditions met, otherwise false
     */
    private boolean checkWinCondition() {
        int noColor = getEmptyColor();

        // loop through each column
        for (int col = 0; col < 4; col++) {

            // to hold the first color in each column
            int referenceColor = -1;

            boolean isColumnValid = true;

            // Loop through rows 2 to 5 (indices 1-4)
            for (int row = 1; row < 5; row++) {
                // the current button
                Button button = getButton(row, col);

                // current button color
                ColorStateList buttonColor = button.getBackgroundTintList();
                assert buttonColor != null;
                int currentColor = buttonColor.getDefaultColor();

                if (referenceColor == -1) {
                    // set the reference color to current element color
                    referenceColor = currentColor;
                } else if (currentColor != referenceColor) {
                    // column colors not the same
                    isColumnValid = false;
                    break;
                }
            }
            // return false if column not the same color
            if (!isColumnValid) {
                return false;
            }
        }
        // all elements in each column are the same color
        return true;
    }

    /**
     * Shows the win dialog box
     */
    private void showWinDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Congratulations! You've won!")
                .setMessage("Would you like to play again?")
                .setPositiveButton("Yes", (dialog, which) -> restartGame())
                .setNegativeButton("Main Menu", (dialog, which) -> returnToMainMenu())
                .setCancelable(false)
                .show();
    }

    /**
     * Restarts the game
     */
    private void restartGame() {
        Intent intent = getIntent();
        finish();
        startActivity(intent);
    }

    /**
     * Returns to the main menu
     */
    private void returnToMainMenu() {
        finish();
    }
}