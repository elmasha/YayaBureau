package com.intech.yayabureau.Service;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.intech.yayabureau.Models.Tokens;

import java.util.HashMap;

public class MyFirebaseIdService extends FirebaseInstanceIdService {

    String refreshToken;
    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();

         refreshToken = FirebaseInstanceId.getInstance().getToken();
        upDateTokenService(refreshToken);
    }

    private void upDateTokenService(String refreshToken) {


        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference tokens = db.collection("Yaya_Bureau");
        HashMap<String,Object> tk = new HashMap<>();
        tk.put("device_token",refreshToken);

        Tokens token = new Tokens(refreshToken);

        if (FirebaseAuth.getInstance().getCurrentUser() != null) {

            String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
            tokens.document(uid).update(tk);


        }
    }
}
