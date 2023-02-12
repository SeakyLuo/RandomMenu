package personalprojects.seakyluo.randommenu.adapters;

import personalprojects.seakyluo.randommenu.R;
import personalprojects.seakyluo.randommenu.models.Address;

public class AddressAdapter extends CustomAdapter<Address> {

    @Override
    public int getLayout(int viewType) {
        return R.layout.view_listed_address;
    }

    @Override
    public void fillViewHolder(CustomViewHolder view, Address data, int position) {

    }

}
