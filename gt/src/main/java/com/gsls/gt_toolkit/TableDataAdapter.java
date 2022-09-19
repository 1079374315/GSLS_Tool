package com.gsls.gt_toolkit;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.gsls.gt.GT;
import com.gsls.gt.R;

import java.util.ArrayList;
import java.util.List;

public class TableDataAdapter extends GT.Adapters.BaseAdapter<String, TableDataAdapter.TableDataAdapterHolder> {

    public List<Integer> bytesList = new ArrayList<>();

    public TableDataAdapter(Context context) {
        super(context);
    }

    @Override
    protected int loadLayout() {
        return R.layout.item_table_all_data;
    }

    @Override
    protected void initView(TableDataAdapterHolder holder, View itemView, int position, String s, Context context) {

        GT.ViewUtils.MarqueeTextView item_sql = (GT.ViewUtils.MarqueeTextView) LayoutInflater.from(getContext()).inflate(R.layout.item_sql_table_tv, null);
        item_sql.setText(s);
        holder.ll_allData.addView(item_sql);

        String[] split = s.split("-GT-");
        for (int i = 0; i < split.length; i++) {
            item_sql = (GT.ViewUtils.MarqueeTextView) LayoutInflater.from(getContext()).inflate(R.layout.item_sql_table_tv, null);
            String data = adaptationCharacter(bytesList.get(i), split[i]); //字符适配
            item_sql.setText(data);
            holder.ll_allData.addView(item_sql);
        }
    }

    @Override
    protected TableDataAdapterHolder onCreateViewHolder(View itemView) {
        return new TableDataAdapterHolder(itemView);
    }

    static class TableDataAdapterHolder extends RecyclerView.ViewHolder {

        private LinearLayout ll_allData;

        public TableDataAdapterHolder(@NonNull View itemView) {
            super(itemView);
            ll_allData = itemView.findViewById(R.id.ll_allData);
        }
    }


    private String adaptationCharacter(int maxLength, String data) {
        if (data == null) return data;
//        GT.log("最大字符：" + maxLength + "当前字符：" + data.getBytes().length);
        maxLength = 15;
        if (data.getBytes().length < maxLength) {
            //小于
            while (true) {
                data += " ";
                if (data.getBytes().length >= maxLength) {
                    break;
                }
            }
        } else if (data.getBytes().length > maxLength) {
            //大于
            if (maxLength > 6) {
                data = data.substring(0, maxLength - 2) + "..";
            } else {
                data = data.substring(0, maxLength);
            }
        }
        return data;
    }


}
