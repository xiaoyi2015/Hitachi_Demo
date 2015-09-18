package ac.airconditionsuit.app.activity;

import ac.airconditionsuit.app.R;
import ac.airconditionsuit.app.util.VibratorUtil;
import android.app.Activity;
import android.content.ClipData;
import android.content.ClipDescription;
import android.graphics.Color;
import android.os.Bundle;
import android.view.DragEvent;
import android.view.View;
import android.widget.ImageView;

public class DragSampleActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drag_sample);
        final ImageView imageView = (ImageView) findViewById(R.id.drag_view);
        imageView.setOnLongClickListener(new View.OnLongClickListener() {
                                             @Override
                                             public boolean onLongClick(View v) {
                                                 VibratorUtil.vibrate(DragSampleActivity.this, 100);


                                                 // Create a new ClipData.
                                                 // This is done in two steps to provide clarity. The convenience method
                                                 // ClipData.newPlainText() can create a plain text ClipData in one step.

                                                 // Create a new ClipData.Item from the ImageView object's tag
                                                 ClipData.Item item = new ClipData.Item("123");

                                                 // Create a new ClipData using the tag as a label, the plain text MIME type, and
                                                 // the already-created item. This will create a new ClipDescription object within the
                                                 // ClipData, and set its MIME type entry to "text/plain"
                                                 ClipData dragData = new ClipData((CharSequence) v.getTag(),
                                                         new String[]{ClipDescription.MIMETYPE_TEXT_PLAIN}, item);
                                                 // Instantiates the drag shadow builder.
                                                 View.DragShadowBuilder myShadow = new View.DragShadowBuilder(imageView);

                                                 // Starts the drag

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
                        imageView.setBackgroundColor(Color.BLUE);
                        return true;
                    case DragEvent.ACTION_DRAG_ENTERED:
                        imageView.setBackgroundColor(Color.GREEN);
                        return true;

                    case DragEvent.ACTION_DRAG_LOCATION:
                        System.out.println(event.getX());
                        System.out.println(event.getY());
                        return true;

                    case DragEvent.ACTION_DRAG_EXITED:
                        imageView.setBackgroundColor(Color.BLACK);
                        return true;

                    case DragEvent.ACTION_DROP:
                        imageView.setBackgroundColor(Color.GRAY);
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
