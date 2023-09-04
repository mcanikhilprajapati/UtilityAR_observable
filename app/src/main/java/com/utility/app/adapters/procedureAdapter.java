package com.utility.app.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.utility.app.listener.OnItemClickListener;
import com.utility.app.models.Observations;
import com.utilityar.app.R;

import java.util.ArrayList;


public class procedureAdapter extends RecyclerView.Adapter<procedureAdapter.ViewHolder> {

    private Context context;
    private ArrayList<Observations> courseModalArrayList;
    private OnItemClickListener onItemClickListener;
    // creating a constructor class.
    public procedureAdapter(Context context, ArrayList<Observations> courseModalArrayList,OnItemClickListener onItemClickListener) {
        this.context = context;
        this.onItemClickListener=onItemClickListener;
        this.courseModalArrayList = courseModalArrayList;
    }

    @NonNull
    @Override
    public procedureAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // passing our layout file for displaying our card item
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.rawitem_observations, parent, false));

    }

    @Override
    public void onBindViewHolder(@NonNull procedureAdapter.ViewHolder holder, int position) {
        // setting data to our text views from our modal class.
        Observations courses = courseModalArrayList.get(position);
        holder.name.setText(courses.getCourseName());
        holder.name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(position);
                }
            }
        });
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return courseModalArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        // creating variables for our text views.
        private final Button name;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            // initializing our text views.
            name = itemView.findViewById(R.id.btn_name);

        }
    }
}