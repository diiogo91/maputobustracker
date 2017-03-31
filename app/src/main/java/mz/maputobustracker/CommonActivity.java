package mz.maputobustracker;

import android.graphics.Color;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;


abstract public class CommonActivity extends AppCompatActivity {

    protected AutoCompleteTextView email;
    protected EditText password;
    protected ProgressBar progressBar;
    protected Snackbar snackbar;

    protected void showSnackbar( String message ){
        snackbar= Snackbar.make(progressBar,
                message,
                Snackbar.LENGTH_LONG)
                .setAction("Action", null);
        View snackBarView = snackbar.getView();
        snackBarView.setBackgroundColor(Color.parseColor("#ff21ab29")); // snackbar background color
        snackbar.setActionTextColor(Color.parseColor("#FFFFEE19")); // snackbar action text color
        snackbar.show();
    }

    protected void showToast( String message ){
        Toast.makeText(this,
                message,
                Toast.LENGTH_LONG)
                .show();
    }

    protected void openProgressBar(){
        progressBar.setVisibility( View.VISIBLE );
    }

    protected void closeProgressBar(){
        progressBar.setVisibility( View.GONE );
    }

    abstract protected void initViews();

    abstract protected void initUser();
}
