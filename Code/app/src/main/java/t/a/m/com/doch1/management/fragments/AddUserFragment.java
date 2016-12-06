package t.a.m.com.doch1.management.fragments;

import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.unnamed.b.atv.model.TreeNode;
import com.unnamed.b.atv.view.AndroidTreeView;

import java.util.UUID;

import t.a.m.com.doch1.Models.Group;
import t.a.m.com.doch1.Models.User;
import t.a.m.com.doch1.R;
import t.a.m.com.doch1.common.validators.EmailValidator;
import t.a.m.com.doch1.common.validators.NotEmptyValidator;
import t.a.m.com.doch1.common.validators.PhoneValidator;

/**
 * Created by Morad on 12/6/2016.
 */
public class AddUserFragment extends Fragment implements View.OnClickListener {

    public static final String TAG = "add_group_fragment";

    private NotEmptyValidator mNameValidator, mPersonalIdValidator;
    private PhoneValidator mPhoneValidator;
    private EmailValidator mEmailValidator;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.add_new_user_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mNameValidator = new NotEmptyValidator((TextInputLayout) view.findViewById(R.id.name_layout));
        mPersonalIdValidator = new NotEmptyValidator((TextInputLayout) view.findViewById(R.id.personal_id_layout));
        mPhoneValidator = new PhoneValidator((TextInputLayout) view.findViewById(R.id.phone_layout));
        mEmailValidator = new EmailValidator((TextInputLayout) view.findViewById(R.id.email_layout));

        view.findViewById(R.id.save_btn).setOnClickListener(this);
        view.findViewById(R.id.cancel_btn).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.save_btn :

                if(mNameValidator.validate() && mEmailValidator.validate()
                        && mPersonalIdValidator.validate() && mPhoneValidator.validate())
                {
                    User user = new User();
                    user.setId(UUID.randomUUID().toString());
                    user.setName(mNameValidator.getValue());
                    user.setEmail(mEmailValidator.getValue());
                    user.setPersonalId(mPersonalIdValidator.getValue());
                    user.setPhone(mPhoneValidator.getValue());

                    if(getActivity() instanceof OnAddUserEvents){
                        ((OnAddUserEvents) getActivity()).onSaveClicked(user);
                    }
                }

                break;
            case R.id.cancel_btn :
                if(getActivity() instanceof OnAddUserEvents){
                    ((OnAddUserEvents) getActivity()).onCancelClicked();
                }
                break;
        }
    }

    public interface OnAddUserEvents{
        void onSaveClicked(User user);
        void onCancelClicked();
    }
}
