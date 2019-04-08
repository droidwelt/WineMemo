package ru.droidwelt.winememo.main_UI;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import ru.droidwelt.winememo.R;
import ru.droidwelt.winememo.WMA;

import static ru.droidwelt.winememo.main_UI.Main_Activity.REC_MODIFIED;


@SuppressWarnings("Annotator")
class MainRecyclerAdapter extends RecyclerView.Adapter<MainRecyclerAdapter.ViewHolder> {


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemLayoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.main_item, parent, false);
        return new ViewHolder(itemLayoutView);
    }


    @Override
    public int getItemCount() {
        return Main_Activity.list_msa.size();
    }

    /*
    public String _id;
    public String barcode;
    public String name;
    public String tip;
    public String country;
    public String region;
    public String consist;
    public String firma;
    public String god;
    public String emk;
    public String alk;
    public String sug;
    public String rating;
    public String price;
    public String dates;
    public String code;  */


    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        MainDataStructure myListItem = Main_Activity.list_msa.get(position);
        viewHolder._id = myListItem._id;
        viewHolder.main_item_name.setText(myListItem.name);
        //    WMA.MSA_POS=Integer.parseInt(myListItem._id);

        String s = "";
        s = WMA.AddString(s, myListItem.tip);
        s = WMA.AddString(s, myListItem.country);
        s = WMA.AddString(s, myListItem.region);
        viewHolder.main_item_tv1.setText(s);

        s = "";
        s = WMA.AddString(s, myListItem.consist);
        s = WMA.AddString(s, myListItem.god);
        s = WMA.AddString(s, myListItem.emk);
        s = WMA.AddString(s, myListItem.alk);
        s = WMA.AddString(s, myListItem.sug);
        s = WMA.AddString(s, myListItem.price);
        viewHolder.main_item_tv2.setText(s);

        viewHolder.main_item_rating.setImageResource(R.drawable.ic_rating_0);
        if (myListItem.rating != null) {
            if (myListItem.rating.equals("1"))
                viewHolder.main_item_rating.setImageResource(R.drawable.ic_rating_1);
            if (myListItem.rating.equals("2"))
                viewHolder.main_item_rating.setImageResource(R.drawable.ic_rating_2);
            if (myListItem.rating.equals("3"))
                viewHolder.main_item_rating.setImageResource(R.drawable.ic_rating_3);
            if (myListItem.rating.equals("4"))
                viewHolder.main_item_rating.setImageResource(R.drawable.ic_rating_4);
            if (myListItem.rating.equals("5"))
                viewHolder.main_item_rating.setImageResource(R.drawable.ic_rating_5);
        }

        if (myListItem.haspict) {
            if (myListItem.pict != null) {
                Bitmap theImage = BitmapFactory.decodeByteArray(myListItem.pict, 0, myListItem.pict.length);
                viewHolder.main_item_graphic.setImageBitmap(theImage);
            } else {
                viewHolder.main_item_graphic.setImageResource(R.drawable.ic_empty_picture);
            }
        } else {
            byte[] favicon = getOneRecordImageValue(myListItem._id);
            if (favicon != null) {
                myListItem.haspict = true;
                myListItem.pict = favicon;
                Main_Activity.list_msa.set(position, myListItem);
                Bitmap theImage = BitmapFactory.decodeByteArray(favicon, 0, favicon.length);
                viewHolder.main_item_graphic.setImageBitmap(theImage);
            } else {
                viewHolder.main_item_graphic.setImageResource(R.drawable.ic_empty_picture);
            }
        }
        viewHolder.itemView.setLongClickable(true);
    }


    private byte[] getOneRecordImageValue(String id) {
        Cursor c = WMA.getDatabase().rawQuery("select pict from contacts where _id=" + id, null);
        c.moveToFirst();
        byte[] res = null;
        try {
            res = c.getBlob(0);
            c.close();
        } catch (Exception ignored) {
        } finally {
            c.close();
        }
        return res;
    }


    static class ViewHolder extends RecyclerView.ViewHolder  {

        final TextView main_item_name;
        final TextView main_item_tv1;
        final TextView main_item_tv2;
        final ImageView main_item_rating;
        final ImageView main_item_graphic;
        String _id;

        ViewHolder(final View itemLayoutView) {
            super(itemLayoutView);
            main_item_name = itemLayoutView.findViewById(R.id.main_item_name);
            main_item_tv1 = itemLayoutView.findViewById(R.id.main_item_tv1);
            main_item_tv2 = itemLayoutView.findViewById(R.id.main_item_tv2);
            main_item_rating = itemLayoutView.findViewById(R.id.main_item_rating);
            main_item_graphic = itemLayoutView.findViewById(R.id.main_item_graphic);
            if (getAdapterPosition() >= 0) {
                WMA.MSA_POS = getAdapterPosition();
                WMA.MSA_ID = Integer.parseInt(Main_Activity.list_msa.get(WMA.MSA_POS)._id);
            }

            itemLayoutView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!_id.isEmpty()) {
                        Intent viewIntent = new Intent(Main_Activity.getInstance(), View_Activity.class);
                        WMA.MSA_ID = Integer.parseInt(_id);
                        WMA.MSA_POS = getAdapterPosition();
                        viewIntent.putExtra("CONTACT_ID", WMA.MSA_ID);
                        Main_Activity.getInstance().startActivityForResult(viewIntent, REC_MODIFIED);
                        WMA.animateStart(Main_Activity.getInstance());
                    }
                }
            });

            itemLayoutView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (!_id.isEmpty()) {
                        WMA.MSA_ID = Integer.parseInt(_id);
                        WMA.MSA_POS = getAdapterPosition();
                        Main_Activity.getInstance().deleteRecord();
                    }
                    return false;
                }
            });
        }
    }
}