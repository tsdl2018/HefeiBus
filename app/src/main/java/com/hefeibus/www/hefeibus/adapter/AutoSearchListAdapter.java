package com.hefeibus.www.hefeibus.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.hefeibus.www.hefeibus.R;
import com.hefeibus.www.hefeibus.entity.Item;
import com.hefeibus.www.hefeibus.utils.Parameters;
import com.hefeibus.www.hefeibus.view.line_detail.LineDetailActivity;
import com.hefeibus.www.hefeibus.view.station_detail.StationDetailActivity;

import java.util.ArrayList;
import java.util.List;

public class AutoSearchListAdapter extends BaseAdapter implements Filterable {

    private final Object mLock = new Object();
    private List<Item> resultSet;
    private Context mContext;
    private ArrayFilter mFilter;
    private List<Item> mOriginalValues;

    public AutoSearchListAdapter(Context context) {
        mContext = context;
    }

    public void setResultSet(List<Item> resultSet) {
        this.resultSet = resultSet;
        this.notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return resultSet == null ? 0 : resultSet.size();
    }

    @Override
    public Object getItem(int position) {
        return resultSet.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final Item item = resultSet.get(position);
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.component_group_line_son, parent, false);
            holder = new ViewHolder();
            holder.itemName = (TextView) convertView.findViewById(R.id.line_name);
            convertView.setTag(holder);   //将Holder存储到convertView中
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.itemName.setText(item.getName());
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                if (item.getIsLine() == 1) {
                    intent.setClass(mContext, LineDetailActivity.class);
                    intent.putExtra(Parameters.INTENT_LINE_KEY, item.getName().substring(0, item.getName().indexOf("路")));
                } else {
                    intent.setClass(mContext, StationDetailActivity.class);
                    intent.putExtra(Parameters.INTENT_STATION_KEY, item.getName());
                }
                mContext.startActivity(intent);
            }
        });
        return convertView;
    }

    @Override
    public Filter getFilter() {
        if (mFilter == null) {
            mFilter = new ArrayFilter();
        }
        return mFilter;
    }

    static class ViewHolder {
        TextView itemName;
    }

    private class ArrayFilter extends Filter {
        //执行刷选
        @Override
        protected FilterResults performFiltering(CharSequence prefix) {
            FilterResults results = new FilterResults();//过滤的结果
            //原始数据备份为空时，上锁，同步复制原始数据
            if (mOriginalValues == null) {
                synchronized (mLock) {
                    mOriginalValues = new ArrayList<>(resultSet);
                }
            }
            //当首字母为空时
            if (prefix == null || prefix.length() == 0) {
                ArrayList<Item> list;
                synchronized (mLock) {//同步复制一个原始备份数据
                    list = new ArrayList<>(mOriginalValues);
                }
                results.values = list;
                results.count = list.size();//此时返回的results就是原始的数据，不进行过滤
            } else {
                String prefixString = prefix.toString().toLowerCase();//转化为小写

                ArrayList<Item> values;
                synchronized (mLock) {//同步复制一个原始备份数据
                    values = new ArrayList<>(mOriginalValues);
                }
                final int count = values.size();
                final ArrayList<Item> newValues = new ArrayList<>();

                for (int i = 0; i < count; i++) {
                    final Item value = values.get(i);//从List<User>中拿到User对象
//                    final String valueText = value.toString().toLowerCase();
                    final String valueText = value.getName().toLowerCase();//User对象的name属性作为过滤的参数
                    // First match against the whole, non-splitted value
                    if (valueText.startsWith(prefixString) || valueText.contains(prefixString)) {//第一个字符是否匹配
                        newValues.add(value);//将这个item加入到数组对象中
                    } else {//处理首字符是空格
                        final String[] words = valueText.split(" ");
                        // Start at index 0, in case valueText starts with space(s)
                        for (String word : words) {
                            if (word.startsWith(prefixString)) {//一旦找到匹配的就break，跳出for循环
                                newValues.add(value);
                                break;
                            }
                        }
                    }
                }
                results.values = newValues;//此时的results就是过滤后的List<User>数组
                results.count = newValues.size();
            }
            return results;
        }

        //刷选结果
        @Override
        protected void publishResults(CharSequence prefix, FilterResults results) {
            //noinspection unchecked
            resultSet = (List<Item>) results.values;//此时，Adapter数据源就是过滤后的Results
            if (results.count > 0) {
                notifyDataSetChanged();//这个相当于从mDatas中删除了一些数据，只是数据的变化，故使用notifyDataSetChanged()
            } else {
                notifyDataSetInvalidated();//当results.count<=0时，此时数据源就是重新new出来的，说明原始的数据源已经失效了
            }
        }
    }
}
