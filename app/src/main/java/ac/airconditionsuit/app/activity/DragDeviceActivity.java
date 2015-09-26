package ac.airconditionsuit.app.activity;

import android.content.ClipData;
import android.content.ClipDescription;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.DragEvent;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageView;

import ac.airconditionsuit.app.R;
import ac.airconditionsuit.app.adapter.DragDeviceAdapter;
import ac.airconditionsuit.app.entity.Section;
import ac.airconditionsuit.app.listener.MyOnClickListener;
import ac.airconditionsuit.app.util.VibratorUtil;
import ac.airconditionsuit.app.view.CommonDeviceView;
import ac.airconditionsuit.app.view.CommonTopBar;

/**
 * Created by Administrator on 2015/9/21.
 */
public class DragDeviceActivity extends BaseActivity {
    private MyOnClickListener myOnClickListener = new MyOnClickListener() {
        @Override
        public void onClick(View v) {
            super.onClick(v);

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.fragment_setting_drag_device);
        super.onCreate(savedInstanceState);
        CommonTopBar commonTopBar = getCommonTopBar();
        Intent intent = getIntent();
        String section = intent.getStringExtra("section");
        Section room_info = Section.getSectionFromJsonString(section);
        commonTopBar.setTitle(room_info.getName());


        GridView gridView = (GridView) findViewById(R.id.receiver);
        DragDeviceAdapter dragDeviceAdapter = new DragDeviceAdapter(this);
        gridView.setAdapter(dragDeviceAdapter);

        final CommonDeviceView drag_view = (CommonDeviceView) findViewById(R.id.device1);
        drag_view.setOnLongClickListener(new View.OnLongClickListener() {
                                             @Override
                                             public boolean onLongClick(View v) {
                                                 VibratorUtil.vibrate(DragDeviceActivity.this, 100);
                                                 ClipData.Item item = new ClipData.Item("123");
                                                 ClipData dragData = new ClipData((CharSequence) v.getTag(),
                                                         new String[]{ClipDescription.MIMETYPE_TEXT_PLAIN}, item);
                                                 // Instantiates the drag shadow builder.
                                                 View.DragShadowBuilder myShadow = new View.DragShadowBuilder(drag_view);

                                                 v.startDrag(dragData,  // the data to be dragged
                                                         myShadow,  // the drag shadow builder
                                                         null,      // no need to use local data
                                                         0          // flags (not currently used, set to 0)
                                                 );
                                                 return false;
                                             }
                                         }

        );

        findViewById(R.id.receiver).setOnDragListener(new View.OnDragListener() {
            @Override
            public boolean onDrag(View v, DragEvent event) {
                switch (event.getAction()) {
                    case DragEvent.ACTION_DRAG_STARTED:
                        return true;
                    case DragEvent.ACTION_DRAG_ENTERED:
                        return true;

                    case DragEvent.ACTION_DRAG_LOCATION:
                        return true;

                    case DragEvent.ACTION_DRAG_EXITED:

                        return true;

                    case DragEvent.ACTION_DROP:
                        return true;

                    case DragEvent.ACTION_DRAG_ENDED:
                        System.out.println(event.getResult());
                        return true;
                    default:
                        return true;
                }
            }
        });


    }
}
