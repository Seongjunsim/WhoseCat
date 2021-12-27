package com.jmsmart.whosecat.view.fragment;

import android.content.Context;
import android.content.Intent;
import androidx.databinding.DataBindingUtil;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.jmsmart.whosecat.R;
import com.jmsmart.whosecat.data.commondata.User;
import com.jmsmart.whosecat.databinding.FragmentSettingBinding;
import com.jmsmart.whosecat.util.SharedPreferencesUtil;
import com.jmsmart.whosecat.view.com.LoginActivity;
import com.jmsmart.whosecat.view.com.MainActivity;
import com.jmsmart.whosecat.view.com.MyPageEditActivity;
import com.jmsmart.whosecat.view.com.PetDeviceManagementActivity;
import com.jmsmart.whosecat.view.com.PetSettingActivity;


public class FragmentSetting extends ListFragment {

    private FragmentSettingBinding binding;

    private Context context;

    private String managementDevice;
    private String modifyMemberInfo;
    private String registerPet;
    private String logout;

    private String[] LIST_MENU;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,@Nullable Bundle savedInstanceState){
        super.onCreateView(inflater,container,savedInstanceState);

        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_setting, container, false);
        context = container.getContext();
        managementDevice = context.getResources().getString(R.string.management_device);
        modifyMemberInfo = context.getResources().getString(R.string.modifyMemberInfo);
        registerPet = context.getResources().getString(R.string.pet_setting);
        logout = context.getResources().getString(R.string.logout);
        LIST_MENU = new String[]{managementDevice, modifyMemberInfo, registerPet, logout};




        setListAdapter(new ArrayAdapter<String>(getActivity(),android.R.layout.simple_list_item_1, LIST_MENU));

        return binding.getRoot();
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id){
        String strText = (String)l.getItemAtPosition(position);

        if(strText.equals(managementDevice)){
            Intent intent = new Intent(getActivity(), PetDeviceManagementActivity.class);
            intent.putExtra("PetInfo", MainActivity.listPetInfo);
            startActivityForResult(intent,100);
        }else if(strText.equals(modifyMemberInfo)){
            User user = MainActivity.user;
            //수정된 정보를 인텐트. 새로입력된 정보를 user에 넣어주면 됨.

            Intent intent = new Intent(getActivity(), MyPageEditActivity.class);
            intent.putExtra("user",user);
            getActivity().startActivityForResult(intent, 200);
        }else if(strText.equals(registerPet)){
            User user = MainActivity.user;
            Intent intent = new Intent(getActivity(), PetSettingActivity.class);
            intent.putExtra("user", user);
            //Intent intent = new Intent(getActivity(), PetRegisterActivty.class);
            //intent.putExtra("type", 0);
            //intent.putExtra("userId",user.userId);
            //intent.putExtra("userName",user.name);
            getActivity().startActivityForResult(intent, 300);
        }else if(strText.equals(logout)){
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            SharedPreferencesUtil.removeLoginInfo(getActivity());
            startActivity(intent);
            getActivity().finish();
        }
    }
}
