package personalprojects.seakyluo.randommenu.adapters.impl;

import android.view.View;

import lombok.Setter;
import personalprojects.seakyluo.randommenu.adapters.CustomAdapter;
import personalprojects.seakyluo.randommenu.interfaces.DataItemClickedListener;
import personalprojects.seakyluo.randommenu.models.vo.ConsumeRecordVO;

public class ConsumeRecordAdapter extends CustomAdapter<ConsumeRecordVO> {

    @Setter
    private DataItemClickedListener<ConsumeRecordVO> onClickListener;

    @Override
    protected int getLayout(int viewType) {
        return 0;
    }

    @Override
    protected void fillViewHolder(CustomViewHolder viewHolder, ConsumeRecordVO data, int position) {
        View view = viewHolder.getView();
        view.setOnClickListener(v -> {
            if (onClickListener != null){
                onClickListener.click(viewHolder, data);
            }
        });
    }
}
