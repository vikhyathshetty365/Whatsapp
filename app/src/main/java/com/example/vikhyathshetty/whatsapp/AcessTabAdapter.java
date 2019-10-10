package com.example.vikhyathshetty.whatsapp;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class AcessTabAdapter extends FragmentPagerAdapter {
    public AcessTabAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int i) {
        switch (i)
        {
            case 0:Chat chat =new Chat();
            return chat;

            case 1:Contact contact=new Contact();
            return contact;

            case 2:
                Group group=new Group();
                return group;

            case 3:
                RequestFragment requestfragment=new RequestFragment();
                return requestfragment;

                default:
                    return null;
        }
    }

    @Override
    public int getCount() {
        return 4;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {

        switch(position){
            case 0:
                return "Chats";

            case 1:
                return "Contacts";

            case 2:
                return "Group";

            case 3:
                return "request";

            default:
                return null;
        }
    }
}
