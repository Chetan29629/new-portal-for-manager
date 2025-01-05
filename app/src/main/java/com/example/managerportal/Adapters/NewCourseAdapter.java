package com.example.managerportal.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.managerportal.Models.NewCourse;
import com.example.managerportal.R;


import java.util.List;

public class NewCourseAdapter extends RecyclerView.Adapter<NewCourseAdapter.ViewHolder> {
    private Context context;
    private List<NewCourse> courseList; // step 1  : create a list <NewCourseModel Class>

    public NewCourseAdapter(List<NewCourse> courseList , Context context) { // Step 2 : Create a constructor
        this.courseList = courseList;
        this.context = context; // context is require for  image Glide
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view  = LayoutInflater.from(parent.getContext()).inflate(R.layout.courses_layout,parent,false); // step 3
        return new ViewHolder(view); // step 4
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        NewCourse course = courseList.get(position); // step 5 NewCourseModel course = courseList.get(position)
        holder.Name.setText(course.getName());

        Glide.with(context)
                .load(course.getImage()) // Load image from URL
                .placeholder(R.drawable.loading) // loading while loading
                .error(R.drawable.error) // Error image if URL fails
                .into(holder.Image);
        //Glide and Picasso library loads the image from the URL asynchronously and displays it in the ImageView.
        //Placeholder and Error Handling:
        //A placeholder image is shown while the image is being loaded.
        //An error image is shown if the image fails to load.
    }

    @Override
    public int getItemCount() {
        return courseList.size();
    }

    //ViewHolder Class
    public static class ViewHolder extends RecyclerView.ViewHolder{

        ImageView Image;
        TextView Name;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            Image = itemView.findViewById(R.id.Image);
            Name = itemView.findViewById(R.id.Name);
        }
    }
}
