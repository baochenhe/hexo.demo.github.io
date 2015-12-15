package packagemanagertest.recyclerviewtest;

import android.graphics.Canvas;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private RecyclerView recyclerView = null;
    private TextView text = null;
    private Button btn = null;
    private int addSum = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        loadData();
        setListener();
    }

    private void initView() {
        text = (TextView) findViewById(R.id.text);
        btn = (Button) findViewById(R.id.btn);
        recyclerView = (RecyclerView) findViewById(R.id.rv);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL); // 设置对其方式
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, R.drawable.list_divider)); //设置分割线
        recyclerView.setItemAnimator(new DefaultItemAnimator()); //设置Item增加、移除动画 这是我比较喜欢的点

    }

    private void loadData() {
        final List<String> list = new ArrayList<>();
        for(int i = 0;i<10;i++)
        {
            list.add(i+ " 号 元素在此");
        }
        MyAdapter adapter = new MyAdapter(list);
        recyclerView.setAdapter(adapter);
    }



    private final static class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {

        private List<String> data = null;

        private AbsListView.OnItemClickListener onItemClickListener = null;


        public MyAdapter(List<String> data) {
            this.data = data;
        }

        public void setOnItemClickListener(AbsListView.OnItemClickListener onItemClickListener) {
            this.onItemClickListener = onItemClickListener;
        }

        /**
         * 创建ViewHolder ， 总是要先创建才使用的吗，这个应该很好理解。
         *
         * @param parent   调用的改Adapter的 RecyclerView 对象
         * @param viewType The view type of the new View. 新视图的类型 （我没看懂）
         * @return
         */
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_item, null);
            ViewHolder viewHolder = new ViewHolder(view);
            return viewHolder;
        }

        /**
         * 这里就开始使用了   其实就是把我们之前写的 getView() 方法分成了两部分
         *
         * @param holder
         * @param position
         */
        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {
            holder.itemView.setBackgroundColor(Color.parseColor("#999933cc"));
            holder.itemView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT));
            holder.textView.setText(getItem(position));
            holder.textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(onItemClickListener!=null)
                    {
                        onItemClickListener.onItemClick(null,holder.textView,position,holder.textView.getId());
                    }
                }
            });
        }


        @Override
        public int getItemCount() {
            return data == null ? 0 : data.size();
        }

        public String getItem(int position)
        {
            return data.get(position);
        }

        private List<String> getData() {
            return data;
        }


        static class ViewHolder extends RecyclerView.ViewHolder {
            private TextView textView;

            public ViewHolder(View itemView) {
                super(itemView);
                textView = (TextView) itemView.findViewById(R.id.tv);
            }
        }
    }


    private void setListener()
    {
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            String temp = null;

            // 滚动状态改变的时候
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();
                int lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition();
                // 恕小生愚昧，我真没明白为什么 这个方法不是 get ...  已经习惯了 get set 刚开始不注意还挺晕的。

                if (newState == RecyclerView.SCROLL_STATE_IDLE) // 滚动停止
                {
                    temp = "滚动停止";
                } else if (newState == RecyclerView.SCROLL_STATE_SETTLING) // 正在滚动
                {
                    temp = "正在滚动";
                } else if (newState == RecyclerView.SCROLL_STATE_DRAGGING) // 触摸滑动
                {
                    temp = "触摸滑动";
                }

                text.setText(temp + " first: " + firstVisibleItemPosition + " last: " + lastVisibleItemPosition);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0) {
                    text.setText(temp + "  向上" + dy);

                } else {
                    text.setText(temp + "  向下" + dy);
                }
            }
        });

        final MyAdapter adapter = (MyAdapter) recyclerView.getAdapter();
        adapter.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                adapter.getData().remove(position);
                adapter.notifyItemRemoved(position);

                //我观察不做这个刷新 数据就会出现异常。。。非常的奇怪。 //如果有发现更好的方法希望能分享下。。
                recyclerView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        adapter.notifyDataSetChanged();
                    }
                },recyclerView.getItemAnimator().getMoveDuration()*2);

            }
        });

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addSum ++;
                adapter.getData().add("新增元素"+addSum+"号");
                adapter.notifyItemRangeChanged(adapter.getItemCount()-1,1);
            }
        });
    }

}
