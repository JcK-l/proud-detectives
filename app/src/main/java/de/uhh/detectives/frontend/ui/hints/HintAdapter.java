package de.uhh.detectives.frontend.ui.hints;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import de.uhh.detectives.frontend.R;

public class HintAdapter extends RecyclerView.Adapter<HintAdapter.HintViewHolder> {

    final Context context;
    final List<HintModel> hintModels;

    public HintAdapter(final Context context, final List<HintModel> hintModels) {
        this.context = context;
        this.hintModels = hintModels;
    }


    @NonNull
    @Override
    public HintAdapter.HintViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final LayoutInflater inflater = LayoutInflater.from(context);
        final View view = inflater.inflate(R.layout.hint_row, parent, false);
        return new HintAdapter.HintViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HintAdapter.HintViewHolder holder, int position) {
        holder.categoryTextView.setText(hintModels.get(position).getCategory());
        holder.descriptionTextView.setText(hintModels.get(position).getText());
        holder.imageView.setImageResource(hintModels.get(position).getImage());
    }

    @Override
    public int getItemCount() {
        return hintModels.size();
    }

    public static class HintViewHolder extends RecyclerView.ViewHolder {
        final ImageView imageView;
        final TextView categoryTextView, descriptionTextView;

        public HintViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.hint_image);
            categoryTextView = itemView.findViewById(R.id.hint_category);
            descriptionTextView = itemView.findViewById(R.id.hint_text);
        }
    }
}
