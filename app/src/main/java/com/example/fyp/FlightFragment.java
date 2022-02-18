package com.example.fyp;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class FlightFragment extends Fragment {

    private EditText depart, arrival;
    private Button calculateFlightButton;
    private FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_flight,container,false);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        depart = view.findViewById(R.id.editTextDepart);
        arrival = view.findViewById(R.id.editTextArrive);
        calculateFlightButton = view.findViewById(R.id.calculateFlightButton);
        firebaseAuth = FirebaseAuth.getInstance();

        calculateFlightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String bdepart = depart.getText().toString().trim();
                String barrival = arrival.getText().toString().trim();

                if (barrival.isEmpty() || bdepart.isEmpty()) {
                    Toast.makeText(getContext(), "Please insure no fields are left blank", Toast.LENGTH_SHORT).show();
                } else {
                    //send the values from fields to api and calculate visualisation from results
//                    URL url = null;
//                    try {
//                        url = new URL("https://api.goclimate.com/v1/flight_footprint?segments[0][origin]=ARN&segments[0][destination]=BCN&segments[1][origin]=BCN&segments[1][destination]=ARN&cabin_class=economy&currencies[]=SEK&currencies[]=USD");
//
//                    URLConnection uc = url.openConnection();
//
//                    String userpass = "311e02419b9ceb0523ef1a5f" + ":";
//                    String basicAuth = "Basic " + javax.xml.bind.DatatypeConverter.printBase64Binary(userpass.getBytes());
//
//                    uc.setRequestProperty ("Authorization", basicAuth);
//                    InputStream in = uc.getInputStream();
//                    Log.e("stream!!!!",in.toString());
//                    } catch (MalformedURLException e) {
//                        e.printStackTrace();
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                    Intent i = new Intent(FlightFragment.this, FlightDisplayActivity.class);
//                    i.putExtra("depart", bdepart);
//                    i.putExtra("arrive", barrival);
//                    // i.putExtra("amount", amount);
//                    startActivity(i);
                }
            }

        });
    }
}
