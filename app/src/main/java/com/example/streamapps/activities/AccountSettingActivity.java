package com.example.streamapps.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.streamapps.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class AccountSettingActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private FirebaseFirestore database;
    private TextView usernameTextView;
    private Button resetPasswordButton;
    private Button subscribeButton;
    private boolean isSubscribed;
    private Context context;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_setting);

        auth = FirebaseAuth.getInstance();
        database = FirebaseFirestore.getInstance();

        context = this;
        usernameTextView= findViewById(R.id.tv_username);
        resetPasswordButton = findViewById(R.id.bu_resetpw);
        subscribeButton = findViewById(R.id.bt_subscribe);

        //LOADING DATABASE
        database.collection("users").document(auth.getCurrentUser().getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.exists()) {
                    isSubscribed = documentSnapshot.getBoolean("isSubscribed");
                    if (isSubscribed)
                        subscribeButton.setText(R.string.unsubscribe);
                    else
                        subscribeButton.setText(R.string.subscribe);
                }
                else{
                    Toast.makeText(context,"Can't find user data",Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(context,"Can't connect to database.",Toast.LENGTH_SHORT).show();
            }
        });

        usernameTextView.setText(auth.getCurrentUser().getEmail());
        resetPasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                auth.sendPasswordResetEmail(auth.getCurrentUser().getEmail())
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(context, "We have sent you instructions to reset your password!", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(context, "Failed to send reset email!", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });

        subscribeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Map<String,Boolean> data = new HashMap<>();
                data.put("isSubscribed",!isSubscribed);
                database.collection("users").document(auth.getCurrentUser().getUid()).set(data);
                isSubscribed =!isSubscribed;
                String text;
                if(isSubscribed)
                    text = "Subscribed!";
                else
                    text = "Unsubscribed!";

                if(!isSubscribed)
                    subscribeButton.setText(R.string.subscribe);
                else
                    subscribeButton.setText(R.string.unsubscribe);

                Toast.makeText(context,text,Toast.LENGTH_SHORT).show();

            }
        });



    }
}
