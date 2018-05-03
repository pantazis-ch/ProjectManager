package com.android.example.projectmanager.detail.specifications;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.example.projectmanager.R;

import java.util.List;


public class SpecificationAdapter extends RecyclerView.Adapter<SpecificationAdapter.SpecificationHolder> {

    public interface SpecificationAdapterOnClickHandler {
        void onClick(Specification spec);
    }

    private final SpecificationAdapterOnClickHandler mClickHandler;

    public List<Specification> specList;

    public SpecificationAdapter(SpecificationAdapterOnClickHandler clickHandler, List<Specification> specList) {
        mClickHandler = clickHandler;
        this.specList = specList;
    }

    @Override
    public SpecificationHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(R.layout.list_item_specification, parent, false);

        return new SpecificationHolder(view);
    }

    @Override
    public void onBindViewHolder(SpecificationHolder holder, int position) {
        Specification spec = specList.get(position);
        holder.specName.setText(spec.getSpecName());
        holder.specNumber.setText(String.valueOf(spec.getSpecNumber() + 1));
    }

    @Override
    public int getItemCount() {
        if(specList!=null) {
            return specList.size();
        } else {
            return 0;
        }
    }

    public class SpecificationHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView specNumber;
        public TextView specName;

        public SpecificationHolder(View itemView) {
            super(itemView);

            specNumber = itemView.findViewById(R.id.tv_specification_number);
            specName = itemView.findViewById(R.id.tv_specification);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int adapterPosition = getAdapterPosition();

            Specification spec = specList.get(adapterPosition);

            mClickHandler.onClick(spec);
        }
    }
}
