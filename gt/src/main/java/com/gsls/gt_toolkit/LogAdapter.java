package com.gsls.gt_toolkit;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.gsls.gt.GT;
import com.gsls.gt.R;

public class LogAdapter extends GT.Adapters.BaseAdapter<String, LogAdapter.LogAdapterHolder> {

    public LogAdapter(Context context) {
        super(context);
    }

    @Override
    protected int loadLayout() {
        return R.layout.item_log;
    }

    @Override
    protected void initView(LogAdapterHolder holder, View itemView, int position, String msg, Context context) {
        holder.tv_data.setText(msg);
    }

    @Override
    protected LogAdapterHolder onCreateViewHolder(View itemView) {
        return new LogAdapterHolder(itemView);
    }

    static class LogAdapterHolder extends RecyclerView.ViewHolder {

        private TextView tv_data;

        public LogAdapterHolder(@NonNull View itemView) {
            super(itemView);
            tv_data = itemView.findViewById(R.id.tv_data);
        }
    }

}
