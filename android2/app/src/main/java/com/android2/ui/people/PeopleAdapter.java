package com.android2.ui.people;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android2.R;
import com.bumptech.glide.Glide;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class PeopleAdapter extends RecyclerView.Adapter<PeopleAdapter.ViewHolder> {
    private List<Person> personList;
    private Context context;

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView name, latitude, longitude;
        public ImageView picture;


        public ViewHolder(View view) {
            super(view);
            name = view.findViewById(R.id.people_name);
            picture = view.findViewById(R.id.people_picture);
            latitude = view.findViewById(R.id.people_latitude);
            longitude = view.findViewById(R.id.people_longitude);
        }
    }

    public PeopleAdapter(List<Person> personList) {
        this.personList = personList;
    }

    public void addContext(Context context)
    {
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.people_item, parent, false);

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Person person = personList.get(position);
        holder.name.setText(person.getName());
        if (person.getPicture() != null) {
            Glide.with(context)
                    .load(person.getPicture())
                    .into(holder.picture);
        }
        holder.latitude.setText("Latitude: " + String.format("%.9f", person.getLatitude()));
        holder.longitude.setText("Longitude: " + String.format("%.9f", person.getLongitude()));
    }

    @Override
    public int getItemCount() {
        return personList.size();
    }
}
