package proyecto.red_chart_v1.providers;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class AuthProvider {
    //Clase que maneja la parte de autenticación
    private FirebaseAuth mAuth;

    public AuthProvider() {
        mAuth = FirebaseAuth.getInstance();
    }


    public void sendCodeAuth(String phone, PhoneAuthProvider.OnVerificationStateChangedCallbacks callback){
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phone,
                60,
                TimeUnit.SECONDS,
                TaskExecutors.MAIN_THREAD,
                callback
        );
    }
    public Task<AuthResult> signInPhone(String verificationId, String code){
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
        return mAuth.signInWithCredential(credential);
    }

    //Método que retorna en la sesion del usuario el UID del usuario de firebase auth
    public String getId(){

        //Si hay sesion devuelve el UID
        if (mAuth.getCurrentUser() != null) {
            return mAuth.getCurrentUser().getUid();

        //No devuelve nada
        } else {
            return null;
        }
    }

    //Retorna la sesión del usuario, sino será nulo
    public FirebaseUser getSessionUser(){
        return mAuth.getCurrentUser();
    }

}
