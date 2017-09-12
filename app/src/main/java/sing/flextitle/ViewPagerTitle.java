package sing.flextitle;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.support.v4.view.ViewPager;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import sing.viewpagerflextitle.R;

public class ViewPagerTitle extends HorizontalScrollView {

    private ArrayList<TextView> textViews = new ArrayList<>();
    private OnTextViewClick onTextViewClick;
    private DynamicLine dynamicLine;
    private ViewPager viewPager;
    private ViewPager.OnPageChangeListener onPageChangeListener;
    private int margin;

    private LinearLayout.LayoutParams parentParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
    private LinearLayout.LayoutParams contentParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    private LinearLayout.LayoutParams textViewParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1);
    private float defaultTextSize;// 默认字体大小
    private int defaultTextColor;// 默认字体颜色
    private int shaderColorStart;// 渐变开始颜色
    private int lineHeight;// 线的高度
    private int shaderColorEnd;// 渐变结束颜色

    private int selectedTextColor;// 选中字体颜色
    private int allTextViewLength;// 所有字体总长度
    private float itemMargins;// Item之间的边距

    public ViewPagerTitle(Context context) {
        this(context, null);
    }

    public ViewPagerTitle(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ViewPagerTitle(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttributeSet(context, attrs);
    }

    private void initAttributeSet(Context context, AttributeSet attrs) {
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.ViewPagerTitle);
        defaultTextColor = array.getColor(R.styleable.ViewPagerTitle_defaultTextViewColor, Color.GRAY);
        selectedTextColor = array.getColor(R.styleable.ViewPagerTitle_selectedTextViewColor, Color.BLACK);
        defaultTextSize = array.getDimension(R.styleable.ViewPagerTitle_defaultTextViewSize, 18);
        itemMargins = array.getDimension(R.styleable.ViewPagerTitle_item_margins, 30);

        shaderColorStart = array.getColor(R.styleable.ViewPagerTitle_line_start_color, Color.parseColor("#ffc125"));
        shaderColorEnd = array.getColor(R.styleable.ViewPagerTitle_line_end_color, Color.parseColor("#ff4500"));
        lineHeight = array.getInteger(R.styleable.ViewPagerTitle_line_height, 20);

        array.recycle();
    }

    /**
     * 初始化时，调用这个方法。ViewPager需要设置Adapter，且titles的数据长度需要与Adapter中的数据长度一置。
     *
     * @param titles    标题1、标题2 etc
     * @param viewPager
     */
    public void initData(String[] titles, ViewPager viewPager) {
        this.viewPager = viewPager;
        createDynamicLine();
        createTextViews(titles);
    }

    public DynamicLine getDynamicLine() {
        return dynamicLine;
    }

    public int getAllTextViewLength() {
        return allTextViewLength;
    }

    public int getMargin() {
        return margin;
    }

    public void addOnPageChangeListener(ViewPager.OnPageChangeListener listener) {
        onPageChangeListener = listener;
        viewPager.addOnPageChangeListener(onPageChangeListener);
    }

    public ArrayList<TextView> getTextView() {
        return textViews;
    }

    private void createDynamicLine() {
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dynamicLine = new DynamicLine(getContext(), shaderColorStart, shaderColorEnd, lineHeight);
        dynamicLine.setLayoutParams(params);
    }

    private void createTextViews(String[] titles) {
        LinearLayout contentLl = new LinearLayout(getContext());
        contentLl.setLayoutParams(parentParams);
        contentLl.setGravity(Gravity.CENTER_VERTICAL);
        contentLl.setOrientation(LinearLayout.VERTICAL);
        addView(contentLl);

        LinearLayout textViewLl = new LinearLayout(getContext());
        textViewLl.setLayoutParams(contentParams);

        margin = getTextViewMargins(titles);

        textViewParams.setMargins(margin, 0, margin, 0);

        for (int i = 0; i < titles.length; i++) {
            TextView textView = new TextView(getContext());
            textView.setText(titles[i]);
            textView.setTextColor(defaultTextColor);
            textView.setTextSize(defaultTextSize);
            textView.setLayoutParams(textViewParams);
            textView.setGravity(Gravity.CENTER);
            textView.setOnClickListener(onClickListener);
            textView.setTag(i);
            textViews.add(textView);
            textViewLl.addView(textView);
        }
        contentLl.addView(textViewLl);
        contentLl.addView(dynamicLine);
    }

    private int getTextViewMargins(String[] titles) {
        float countLength = 0;
        TextView textView = new TextView(getContext());
        textView.setTextSize(defaultTextSize);
        TextPaint paint = textView.getPaint();

        for (int i = 0; i < titles.length; i++) {
            countLength = countLength + itemMargins + paint.measureText(titles[i]) + itemMargins;
        }
        int screenWidth = Tool.getScreenWidth(getContext());

        if (countLength <= screenWidth) {
            allTextViewLength = screenWidth;
            return (screenWidth / titles.length - (int) paint.measureText(titles[0])) / 2;
        } else {
            allTextViewLength = (int) countLength;
            return (int) itemMargins;
        }
    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            setCurrentItem((int) v.getTag());
            viewPager.setCurrentItem((int) v.getTag());
            if (onTextViewClick != null) {
                onTextViewClick.textViewClick((TextView) v, (int) v.getTag());
            }
        }
    };

    public void setDefaultIndex(int index) {
        setCurrentItem(index);
    }

    public void setCurrentItem(int index) {
        for (int i = 0; i < textViews.size(); i++) {
            textViews.get(i).setTextColor(defaultTextColor);
        }

        textViews.get(index).setTextColor(selectedTextColor);
    }

    public interface OnTextViewClick {
        void textViewClick(TextView textView, int index);
    }

    public void setOnTextViewClickListener(OnTextViewClick onTextViewClick) {
        this.onTextViewClick = onTextViewClick;
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        viewPager.removeOnPageChangeListener(onPageChangeListener);
    }

    public void setMargin(int margin) {
        this.margin = margin;
    }

    public void setDefaultTextSize(float defaultTextSize) {
        this.defaultTextSize = defaultTextSize;
    }

    public void setDefaultTextColor(int defaultTextColor) {
        this.defaultTextColor = defaultTextColor;
    }

    public void setSelectedTextColor(int selectedTextColor) {
        this.selectedTextColor = selectedTextColor;
    }

    public void setAllTextViewLength(int allTextViewLength) {
        this.allTextViewLength = allTextViewLength;
    }

    public void setItemMargins(float itemMargins) {
        this.itemMargins = itemMargins;
    }
}