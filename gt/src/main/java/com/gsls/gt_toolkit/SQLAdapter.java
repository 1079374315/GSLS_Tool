package com.gsls.gt_toolkit;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.gsls.gt.GT;
import com.gsls.gt.R;

public class SQLAdapter extends GT.Adapters.BaseAdapter<String, SQLAdapter.SQLAdapterHolder>{

    private ClickSqlTable clickSqlTable;

    public SQLAdapter(Context context, ClickSqlTable clickSqlTable) {
        super(context);
        this.clickSqlTable = clickSqlTable;
    }

    @Override
    protected int loadLayout() {
        return R.layout.item_sql;
    }

    @Override
    protected void initView(SQLAdapterHolder holder, View itemView, int position, String s, Context context) {
        holder.tv_sql.setText(s);
        //单击事件
        holder.tv_sql.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickSqlTable.clickTable(s);
            }
        });
    }


    @Override
    protected SQLAdapterHolder onCreateViewHolder(View itemView) {
        return new SQLAdapterHolder(itemView);
    }

    static class SQLAdapterHolder extends RecyclerView.ViewHolder{

        private TextView tv_sql;

        public SQLAdapterHolder(@NonNull View itemView) {
            super(itemView);
            tv_sql = itemView.findViewById(R.id.tv_sql);
        }
    }

    public interface ClickSqlTable {
        public void clickTable(String name);
    }
}