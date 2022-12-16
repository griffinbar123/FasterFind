package my.first.fastfinder;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SignUpActivty extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_activty);
        setTitle("Sign Up");

        handleSignUp();
        handleLogInClick();
    }
    void handleLogInClick() {
        TextView not_filler_text = findViewById(R.id.not_filler_text);
        not_filler_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SignUpActivty.this, LoginActivity.class));
//              finish();
            }
        });
    }

    void handleSignUp(){
        TextView signup_button = findViewById(R.id.signup_button);
        TextView signup_email_2 = findViewById(R.id.signup_email_2);
        TextView signup_password_2 = findViewById(R.id.signup_password_2);
        signup_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            if(TextUtils.isEmpty(signup_email_2.getText().toString().trim())) {
                Toast.makeText(SignUpActivty.this, "Please enter email", Toast.LENGTH_SHORT).show();
            } else if(TextUtils.isEmpty(signup_password_2.getText().toString().trim())){
                Toast.makeText(SignUpActivty.this, "Please enter password", Toast.LENGTH_SHORT).show();
            } else {
                String email = signup_email_2.getText().toString().trim();
                String password = signup_password_2.getText().toString().trim();
                FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    FirebaseUser firebaseUser = task.getResult().getUser();
                                    Toast.makeText(SignUpActivty.this, "You are signed up!", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(SignUpActivty.this, MainActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);
                                } else {
                                    Toast.makeText(SignUpActivty.this, task.getException().getMessage().toString(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                }
            }
        });
    }
}