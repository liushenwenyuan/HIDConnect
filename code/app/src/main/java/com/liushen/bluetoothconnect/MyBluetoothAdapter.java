package com.liushen.bluetoothconnect;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.liushen.bluetoothconnect.utils.HidConncetUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liushen on 2016/5/31.
 */
public class MyBluetoothAdapter extends BaseAdapter {
    private List<BluetoothDevice> mList = new ArrayList<>();
    private Context mContext;
    private HidConncetUtil mHidConncetUtil;
    public MyBluetoothAdapter(List<BluetoothDevice> mList, Context mContext) {
        this.mList = mList;
        this.mContext = mContext;
        //4.0以上才支持HID模式
        if (Build.VERSION.SDK_INT >= 14) {
            this.mHidConncetUtil = new HidConncetUtil(mContext);
        }
    }
    public MyBluetoothAdapter( Context mContext) {
        this.mContext = mContext;
        //4.0以上才支持HID模式
        if (Build.VERSION.SDK_INT >= 14) {
            this.mHidConncetUtil = new HidConncetUtil(mContext);
        }
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void addData(BluetoothDevice bluetoothDevice){
        mList.add(bluetoothDevice);
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if(convertView==null){
            convertView = View.inflate(mContext,R.layout.blue_list_item,null);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) convertView.getTag();
        }
        initData(position,viewHolder);
        return convertView;
    }

    private void initData(int position,ViewHolder viewHolder) {
        final BluetoothDevice bluetoothDevice = mList.get(position);
        viewHolder.bluehandlename.setText(bluetoothDevice.getName());
        isBonded(bluetoothDevice,viewHolder);
        isConnected(bluetoothDevice,viewHolder);
        viewHolder.connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mHidConncetUtil != null) {
                    //先配对再连接
                    mHidConncetUtil.pair(bluetoothDevice);
                    mHidConncetUtil.connect(bluetoothDevice);
                }
            }
        });
    }

    /**
     * 判断是否配对并设置配置状态
     * @param bluetoothDevice
     */
    private void isBonded(BluetoothDevice bluetoothDevice,ViewHolder viewHolder){
        int state =  bluetoothDevice.getBondState();
        switch (state) {
            case BluetoothDevice.BOND_NONE:
                viewHolder.bluehandlebond.setText("未配对");
                break;
            case BluetoothDevice.BOND_BONDING:
                viewHolder.bluehandlebond.setText("配对中...");
                break;
            case BluetoothDevice.BOND_BONDED:
                viewHolder.bluehandlebond.setText("已配对");
                break;
        }
    }

    /**
     * 判断是否连接
     * @param bluetoothDevice
     * @param viewHolder
     */
    private void isConnected(final BluetoothDevice bluetoothDevice,final ViewHolder viewHolder){
        if (mHidConncetUtil != null) {
            mHidConncetUtil.getHidConncetList(new HidConncetUtil.GetHidConncetListListener() {
                @Override
                public void getSuccess(ArrayList<BluetoothDevice> list) {
                    //判断连接列表中是否有该设备
                    for(BluetoothDevice bluetoothDevice1:list){
                        if(bluetoothDevice.getAddress().equals(bluetoothDevice1.getAddress())){
                            viewHolder.bluehandlebond.setText("HID已连接");
                            break;
                        }
                    }
                }
            });
        }
    }
    private void connect(BluetoothDevice bluetoothDevice){
        if (mHidConncetUtil != null) {
            mHidConncetUtil.connect(bluetoothDevice);
        }
    }

    public class ViewHolder {
        public final TextView bluehandlename;
        public final TextView bluehandlebond;
        public final Button connect;
        public final View root;

        public ViewHolder(View root) {
            bluehandlename = (TextView) root.findViewById(R.id.blue_handle_name);
            bluehandlebond = (TextView) root.findViewById(R.id.blue_handle_bond);
            connect = (Button) root.findViewById(R.id.connect);
            this.root = root;
        }
    }
}
