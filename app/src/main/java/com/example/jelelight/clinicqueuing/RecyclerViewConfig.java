package com.example.jelelight.clinicqueuing;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class RecyclerViewConfig {

    private Context mContext;
    private QueueAdapter mQueueAdapter;

    public void setConfig(RecyclerView recyclerView, Context context, List<Queue> queues, List<String> keys){
        mContext = context;
        mQueueAdapter = new QueueAdapter(queues,keys);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setAdapter(mQueueAdapter);
    }

    class QueueItemView extends RecyclerView.ViewHolder {

        private TextView mCurrent;
        private TextView mStatus;
        private TextView mPatient;
        private String key;

        public QueueItemView(final ViewGroup parent){
            super(LayoutInflater.from(mContext).inflate(R.layout.queue_list_item,parent,false));
            mCurrent = (TextView) itemView.findViewById(R.id.current_txtview);
            mStatus = (TextView) itemView.findViewById(R.id.status_txtview);
            mPatient = (TextView) itemView.findViewById(R.id.name_txtview);

        }

        public void bind(Queue queue,String key){
            mCurrent.setText(key);
            mStatus.setText(queue.getStatus());
            mPatient.setText(queue.getPatientID());
            this.key = key;
        }

    }

    class QueueAdapter extends RecyclerView.Adapter<QueueItemView>{
        private List<Queue> mQueueList;
        private List<String> mKeys;

        public QueueAdapter(List<Queue> mQueueList, List<String> mKeys) {
            this.mQueueList = mQueueList;
            this.mKeys = mKeys;
        }

        @NonNull
        @Override
        public QueueItemView onCreateViewHolder(@NonNull ViewGroup parent, int i) {
            return new QueueItemView(parent);
        }

        @Override
        public void onBindViewHolder(@NonNull QueueItemView queueItemView, int i) {
            queueItemView.bind(mQueueList.get(i), mKeys.get(i));
        }

        @Override
        public int getItemCount() {
            return mQueueList.size();
        }
    }

}