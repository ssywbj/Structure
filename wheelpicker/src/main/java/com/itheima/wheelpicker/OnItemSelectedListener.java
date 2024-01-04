package com.itheima.wheelpicker;

/**
 * 滚轮选择器Item项被选中时监听接口
 *
 * @author AigeStudio 2016-06-17
 * 新项目结构
 * @version 1.1.0
 */
public interface OnItemSelectedListener {
    /**
     * 当滚轮选择器数据项被选中时回调该方法
     * 滚动选择器滚动停止后会回调该方法并将当前选中的数据和数据在数据列表中对应的位置返回
     *
     * @param picker   滚轮选择器
     * @param data     当前选中的数据
     * @param position 当前选中的数据在数据列表中的位置
     */
    void onItemSelected(WheelPicker picker, Object data, int position);
}