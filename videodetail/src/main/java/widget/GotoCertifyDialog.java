package widget;

import android.app.Dialog;
import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;

public class GotoCertifyDialog extends Dialog {


    public GotoCertifyDialog(@NonNull Context context) {
        super(context);
    }

    public GotoCertifyDialog(@NonNull Context context, View view) {
        super(context);
        super.setContentView(view);
    }



}
