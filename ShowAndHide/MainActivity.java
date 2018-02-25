package com.example.alexzander.showandhide;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    RadioButton showRadioButton;
    RadioButton hideRadioButton;
    TextView helloWorldText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        showRadioButton = findViewById(R.id.showRadioButton);
        hideRadioButton = findViewById(R.id.hideRadioButton);
        helloWorldText  = findViewById(R.id.helloWorldTextView);
    }

    public void onRadiButtonClick(View view) {
        boolean checked = ((RadioButton) view).isChecked();

        switch (view.getId()) {
            case R.id.showRadioButton:
                if (checked) {
                    showRadioButton.setChecked(true);
                    hideRadioButton.setChecked(false);

                    if (helloWorldText.getVisibility() == View.INVISIBLE) {
                        helloWorldText.setVisibility(View.VISIBLE);
                    }

                    break;
                }

            case R.id.hideRadioButton:
                if (checked) {
                    showRadioButton.setChecked(false);
                    hideRadioButton.setChecked(true);

                    if (helloWorldText.getVisibility() == View.VISIBLE) {
                        helloWorldText.setVisibility(View.INVISIBLE);
                    }
                    break;
                }
        }
    }

}
