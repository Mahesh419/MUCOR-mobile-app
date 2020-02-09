package com.id.socketio;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;

public class AddUserActivity extends AppCompatActivity {

    private Button setNickName;
    //private EditText userNickName;
    public static final String TAG  = "NEW_USER";
    private Socket mSocket;
    {
        try {
            mSocket = IO.socket("https://mucor-ws.herokuapp.com/");
        } catch (URISyntaxException e) {}
    }

    public int numberOfLines = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_user);

        //userNickName = findViewById(R.id.userNickName);
        setNickName = findViewById(R.id.setNickName);


        setNickName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mSocket.connect();
                mSocket.on("connect_user", onNewUser);

                String usernameList = String.valueOf(((EditText)findViewById(1)).getText());

                for (int i = 2; i <= numberOfLines; i++){
                    EditText linkedInId = findViewById(i);

                    usernameList += "," + linkedInId.getText();
                }

                JSONObject userId = new JSONObject();
                try {
                    userId.put("usernames", usernameList);
                    Emitter emitter = mSocket.emit("connect_user", userId);

                } catch (Exception e) {
                    e.printStackTrace();
                }

//                Intent intent = new Intent(AddUserActivity.this, MainActivity.class);
//                intent.putExtra("username", userNickName.getText().toString());
//                startActivity(intent);
            }
        });

        final Button Add_button = (Button) findViewById(R.id.add_button);
        Add_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Add_Line();
            }
        });

        Add_Line();
    }

    public void Add_Line() {
        LinearLayout ll = (LinearLayout)findViewById(R.id.linearLayoutDecisions);
        // add edittext
        EditText et = new EditText(this);

        et.setWidth(150);
        et.setHeight(100);
        et.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(500, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.TEXT_ALIGNMENT_GRAVITY);

        et.setLayoutParams(p);
        et.setHint("Enter LinkedIn id here...");
        et.setId(numberOfLines + 1);
        ll.addView(et);
        numberOfLines++;
    }

    private void refresh(){
        Intent intent = new Intent(AddUserActivity.this, AddUserActivity.class);
        startActivity(intent);
    }

    Emitter.Listener onNewUser = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    int length = args.length;

                    if(length == 0){
                        return;
                    }
                    //Here i'm getting weird error..................///////run :1 and run: 0
                    Log.i(TAG, "run: ");
                    Log.i(TAG, "run: " + args.length);
                    String username =args[0].toString();
                    try {
                        JSONObject object = new JSONObject(username);
                        username = object.getString("usernames");

                        if(username != null){
                            showMessageDialog(true);
                        }

                        System.out.println("Username: " + username);
                    } catch (JSONException e) {
                        System.out.println(e);
                        e.printStackTrace();
                        showMessageDialog(false);
                    }

                    Log.i(TAG, "run: " + username);
                }
            });
        }
    };

    private void showMessageDialog(boolean isSuccessfull){

        if(isSuccessfull){
            AlertDialog.Builder builder
                            = new AlertDialog
                            .Builder(AddUserActivity.this);

                    builder.setTitle("Success");

                    builder.setMessage("IDs sent successfully!");

                    builder.setPositiveButton(
                            "Ok",
                            new DialogInterface
                                    .OnClickListener(){

                                @Override
                                public void onClick(DialogInterface dialog,
                                                    int which)
                                {
                                    refresh();
                                }
                            });

                    AlertDialog alertDialog = builder.create();

                    alertDialog.show();

        }else{
            AlertDialog.Builder builder
                    = new AlertDialog
                    .Builder(AddUserActivity.this);

            builder.setTitle("Error");

            builder.setMessage("IDs sending failed!");

            builder.setPositiveButton(
                    "Ok",
                    new DialogInterface
                            .OnClickListener(){

                        @Override
                        public void onClick(DialogInterface dialog,
                                            int which)
                        {
                            //refresh();
                        }
                    });

            AlertDialog alertDialog = builder.create();

            alertDialog.show();
        }
    }
}
