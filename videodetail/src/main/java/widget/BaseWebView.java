package widget;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.just.agentweb.AgentWebView;

public class BaseWebView extends AgentWebView {
    public BaseWebView(@NonNull Context context) {
        super(context);
    }

    public BaseWebView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }
}
