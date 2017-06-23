package com.ucloud.uvod.example.ui;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.ucloud.uvod.example.R;

import java.util.Locale;

import merge.tv.danmaku.ijk.media.player.misc.ITrackInfo;

public class TracksFragment extends Fragment {

    private ListView trackListView;

    private TrackAdapter trackAdapter;

    public static TracksFragment newInstance() {
        TracksFragment f = new TracksFragment();
        return f;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup viewGroup = (ViewGroup) inflater.inflate(R.layout.fragment_track_list, container, false);
        trackListView = (ListView) viewGroup.findViewById(R.id.track_list_view);
        return viewGroup;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        final Activity activity = getActivity();

        trackAdapter = new TrackAdapter(activity);
        trackListView.setAdapter(trackAdapter);

        if (activity instanceof ITrackHolder) {
            final ITrackHolder trackHolder = (ITrackHolder) activity;
            trackAdapter.setTrackHolder(trackHolder);

            int selectedVideoTrack = trackHolder.getSelectedTrack(ITrackInfo.MEDIA_TRACK_TYPE_VIDEO);
            int selectedAudioTrack = trackHolder.getSelectedTrack(ITrackInfo.MEDIA_TRACK_TYPE_AUDIO);
            if (selectedVideoTrack >= 0) {
                trackListView.setItemChecked(selectedVideoTrack, true);
            }
            if (selectedAudioTrack >= 0) {
                trackListView.setItemChecked(selectedAudioTrack, true);
            }

            trackListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, final int position, final long id) {
                    TrackItem trackItem = (TrackItem) trackListView.getItemAtPosition(position);
                    for (int i = 0; i < trackAdapter.getCount(); ++i) {
                        TrackItem compareItem = trackAdapter.getItem(i);
                        if (compareItem.index == trackItem.index) {
                            continue;
                        }

                        if (compareItem.trackInfo.getTrackType() != trackItem.trackInfo.getTrackType()) {
                            continue;
                        }

                        if (trackListView.isItemChecked(i)) {
                            trackListView.setItemChecked(i, false);
                        }
                    }
                    if (trackListView.isItemChecked(position)) {
                        trackHolder.selectTrack(trackItem.index);
                    }
                    else {
                        trackHolder.deselectTrack(trackItem.index);
                    }
                }
            });
        }
        else {
            Log.e("TracksFragment", "activity is not an instance of ITrackHolder.");
        }
    }

    public interface ITrackHolder {
        ITrackInfo[] getTrackInfo();
        int getSelectedTrack(int trackType);
        void selectTrack(int stream);
        void deselectTrack(int stream);
    }

    final class TrackItem {
        public int index;
        public ITrackInfo trackInfo;

        public String infoInline = "";

        TrackItem(int index, ITrackInfo trackInfo) {
            this.index = index;
            this.trackInfo = trackInfo;
            infoInline = String.format(Locale.US, "# %d: %s", this.index, this.trackInfo.getInfoInline());
        }

        public String getInfoInline() {
            return infoInline;
        }
    }

    final class TrackAdapter extends ArrayAdapter<TrackItem> {
        private ITrackHolder trackHolder;
        private ITrackInfo[] trackInfos;

        TrackAdapter(Context context) {
            super(context, android.R.layout.simple_list_item_checked);
        }

        public void setTrackHolder(ITrackHolder trackHolder) {
            clear();
            this.trackHolder = trackHolder;
            trackInfos = this.trackHolder.getTrackInfo();
            if (trackInfos != null) {
                for (ITrackInfo trackInfo: trackInfos) {
                    int index = getCount();
                    TrackItem item = new TrackItem(index, trackInfo);
                    add(item);
                }
            }
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = convertView;
            if (view == null) {
                LayoutInflater inflater = LayoutInflater.from(parent.getContext());
                view = inflater.inflate(android.R.layout.simple_list_item_checked, parent, false);
            }

            ViewHolder viewHolder = (ViewHolder) view.getTag();
            if (viewHolder == null) {
                viewHolder = new ViewHolder();
                viewHolder.nameTextView = (TextView) view.findViewById(android.R.id.text1);
            }

            TrackItem item = getItem(position);
            viewHolder.nameTextView.setText(item.getInfoInline());

            return view;
        }

        final class ViewHolder {
            public TextView nameTextView;
        }
    }
}
