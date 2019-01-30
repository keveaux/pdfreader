package com.example.keveaux_tm.pdfreader.PaymentActivities;

import android.app.ProgressDialog;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.keveaux_tm.pdfreader.R;
import com.twigafoods.daraja.Daraja;
import com.twigafoods.daraja.DarajaListener;
import com.twigafoods.daraja.model.AccessToken;
import com.twigafoods.daraja.model.LNMExpress;
import com.twigafoods.daraja.model.LNMResult;

public class MpesaActivity extends AppCompatActivity {

    EditText editTextPhoneNumber;
    Button sendButton;
    Daraja daraja;
    String phoneNumber;
    int amount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mpesa);

        editTextPhoneNumber =findViewById(R.id.editTextPhoneNumber);
        sendButton=findViewById(R.id.sendButton);

        //get amount from ShoppingCartActivity
        amount=getIntent().getExtras().getInt("amount");

        final ProgressDialog pd = new ProgressDialog(MpesaActivity.this);
        pd.setMessage("loading");
        pd.show();

        //Init Daraja
        //TODO :: REPLACE WITH YOUR OWN CREDENTIALS  :: THIS IS SANDBOX DEMO
        daraja = Daraja.with("ZEFCtXqQGqxjHg62NWSArWwJGwHxw3g3", "tGFlKH71AkHvhJCW",
                new DarajaListener<AccessToken>() {
                    @Override
                    public void onResult(@NonNull AccessToken accessToken) {
                        Log.i(MpesaActivity.this.getClass().getSimpleName(), accessToken.getAccess_token());
                        Toast.makeText(MpesaActivity.this, "TOKEN : " + accessToken.getAccess_token(), Toast.LENGTH_SHORT).show();
                        pd.dismiss();
                    }

                    @Override
                    public void onError(String error) {
                        Log.e(MpesaActivity.this.getClass().getSimpleName(), error);
                    }});

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Get Phone Number from User Input
                phoneNumber = editTextPhoneNumber.getText().toString().trim();

                if (TextUtils.isEmpty(phoneNumber)) {
                    editTextPhoneNumber.setError("Please Provide a Phone Number");
                    return;
                }

                //TODO :: REPLACE WITH YOUR OWN CREDENTIALS  :: THIS IS SANDBOX DEMO
                LNMExpress lnmExpress = new LNMExpress(
                        "174379",
                        "bfb279f9aa9bdbcf158e97dd71a467cd2e0c893059b10f78e6b72ada1ed2c919",
                        //https://developer.safaricom.co.ke/test_credentials
                        "1",
                        "254708374149",
                        "174379",
                        phoneNumber,
                        "http://104.248.124.210/pdfwork/pdfwork/mpesastuff/checkout.php",
                        "001ABC",
                        "Goods Payment"
                );

                daraja.requestMPESAExpress(lnmExpress,
                        new DarajaListener<LNMResult>() {
                            @Override
                            public void onResult(@NonNull LNMResult lnmResult) {
                                Log.i("hello", lnmResult.ResponseDescription);
                                Toast.makeText(MpesaActivity.this, ""+lnmResult.ResponseDescription, Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onError(String error) {
                                Log.i(MpesaActivity.this.getClass().getSimpleName(), error);
                            }
                        }
                );
            }
        });
    }
}
