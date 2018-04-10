package kogvet.eye.LoginFragment;
import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import kogvet.eye.MainActivity;
import kogvet.eye.R;


/**
 * Fragment class for Home Fragment
 */
public class FragmentLogin extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        ((MainActivity) getActivity()).showBackButton();
        ((MainActivity) getActivity()).setActionBarTitle(getResources().getString(R.string.title_login));

        return inflater.inflate(R.layout.fragment_login, container, false);
    }
}
