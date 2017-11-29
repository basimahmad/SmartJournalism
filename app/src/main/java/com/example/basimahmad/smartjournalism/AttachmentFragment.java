package com.example.basimahmad.smartjournalism;

/**
 * Created by Basim Ahmad on 11/6/2017.
 */
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;
import com.afollestad.materialdialogs.MaterialDialog;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import Tempelate.Template;

import java.io.File;
import java.util.ArrayList;

import static android.app.Activity.RESULT_OK;

public class AttachmentFragment extends Fragment{
    View view;
    private static String[] CHOOSE_FILE = {"Photo", "Video", "File manager"};
    private Button mAdd, mUpload;
    private ImageView mImage, mImage2, mImage3, mImage4, mImage5, mImage6, mImage7, mImage8, mImage9, mController;
    private VideoView mVideo;
    private TextView mInfo, mResponse;
    private ProgressBar mProgress;
    private Uri mOutputUri;
    private ArrayList<File> mFile = new ArrayList<File>();
    public ArrayList<String> file_names = new ArrayList<String>();
    private RequestQueue mRequest;
    private MultiPartRequest mMultiPartRequest;
    private MediaPlayer mMediaPlayer;
    private boolean mIsLoad = false;
    public AttachmentFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_attachment, container, false);
        VolleySingleton volleySingleton = new VolleySingleton(getActivity());
        mRequest = volleySingleton.getInstance().getRequestQueue();
        mAdd = (Button) view.findViewById(R.id.add);
        mUpload = (Button) view.findViewById(R.id.upload);
        mImage = (ImageView) view.findViewById(R.id.image);
        mImage2 = (ImageView) view.findViewById(R.id.image2);
        mImage3 = (ImageView) view.findViewById(R.id.image3);


        mController = (ImageView) view.findViewById(R.id.controller);
        mVideo = (VideoView) view.findViewById(R.id.video);
        mProgress = (ProgressBar) view.findViewById(R.id.progress);

        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                    .permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        System.setProperty("http.keepAlive", "false");

        //Set video view untuk looping video
        mVideo.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                mediaPlayer.setLooping(true);
            }
        });

        mInfo = (TextView) view.findViewById(R.id.file_info);
        mResponse = (TextView) view.findViewById(R.id.response);
        resetView();

        //Set add button listener
        mAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (((TextView) view).getText().equals("Delete")) {
                    resetView();
                    if (mIsLoad) {
                        mRequest.cancelAll("MultiRequest");
                        mRequest.stop();
                        mIsLoad = false;
                    }

                } else {
                    showDialog();
                }

            }
        });

        //Set upload button listener
        mUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadFile();
                mUpload.setVisibility(Button.INVISIBLE);
                mProgress.setVisibility(ProgressBar.VISIBLE);
                mIsLoad = true;

            }
        });
        return view;
    }

    //Respon dari add button ketika diklik, untuk memunculkan dialog
    void showDialog() {
        new MaterialDialog.Builder(getActivity()).title("Choose file")
                .items(CHOOSE_FILE)
                .itemsCallback(new MaterialDialog.ListCallback() {
                    @Override
                    public void onSelection(MaterialDialog materialDialog, View view, int i, CharSequence charSequence) {
                        if (i == 0) {
                            //Mengambil foto dengan camera
                            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                            mOutputUri = FileManager.getOutputMediaFileUri(Template.Code.CAMERA_IMAGE_CODE);

                            intent.putExtra(MediaStore.EXTRA_OUTPUT, mOutputUri);


                            startActivityForResult(intent, Template.Code.CAMERA_IMAGE_CODE);
                        } else if (i == 1) {
                            //Mengambil video dengan camera
                            Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);

                            mOutputUri = FileManager.getOutputMediaFileUri(Template.Code.CAMERA_VIDEO_CODE);


                            intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);

                            intent.putExtra(MediaStore.EXTRA_OUTPUT, mOutputUri);
                            startActivityForResult(intent, Template.Code.CAMERA_VIDEO_CODE);
                        } else {
                            //Mendapatkan file dari storage
                            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                            intent.setType("image/* video/* audio/*");
                            startActivityForResult(intent, Template.Code.FILE_MANAGER_CODE);
                        }
                    }
                }).show();
    }

    //Respon dari upload button ketika diklik, untuk melakukan upload file ke server
    void uploadFile() {
        mRequest.start();
        mMultiPartRequest = new MultiPartRequest(new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mUpload.setVisibility(Button.VISIBLE);
                mProgress.setVisibility(ProgressBar.GONE);
                mIsLoad = false;
                Log.d("sa", String.valueOf(error));
                setResponse(null, error);
            }
        }, new Response.Listener() {
            @Override
            public void onResponse(Object response) {
                mUpload.setVisibility(Button.VISIBLE);
                mProgress.setVisibility(ProgressBar.GONE);
                mIsLoad = false;
                Log.d("sa", String.valueOf(response));
                setResponse(response, null);

            }
        }, mFile, mFile.size());
        //Set tag, diperlukan ketika akan menggagalkan request/cancenl request
        mMultiPartRequest.setTag("MultiRequest");
        //Set retry policy, untuk mengatur socket time out, retries. Bisa disetting lewat template
        mMultiPartRequest.setRetryPolicy(new DefaultRetryPolicy(Template.VolleyRetryPolicy.SOCKET_TIMEOUT,
                Template.VolleyRetryPolicy.RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        //Menambahkan ke request queue untuk diproses
        mRequest.add(mMultiPartRequest);
    }

    //Mengisi variable File dari path yang didapat dari storage
    void setFile(int type, Uri uri) {
        mFile.add(new File(FileManager.getPath(getActivity().getApplicationContext(), type, uri)));
    }

    //Respon ketika path file dari storage didapatkan, untuk menampilkan view untuk upload
    void setView(int type, Uri uri) {
        mUpload.setVisibility(Button.VISIBLE);
        if (mFile.size() == 4) {
            Toast.makeText(getActivity(), "3 files already selected", Toast.LENGTH_SHORT).show();
            mAdd.setEnabled(false);
            return ;
        }
        mInfo.setVisibility(TextView.VISIBLE);
        int size = mFile.size();
        mInfo.setText("File info\n" + "Name : " + mFile.get(size-1).getName() + "\nSize : " +
             FileManager.getSize(mFile.get(size-1).length(), true));
        if (type == Template.Code.CAMERA_IMAGE_CODE) {
            if (mFile.size() == 1) {
                mImage.setVisibility(ImageView.VISIBLE);
                mImage.setImageBitmap(BitmapFactory.decodeFile(FileManager.getPath(getActivity().getApplicationContext(), type, uri)));
            } else if (mFile.size() == 2) {
                mImage2.setVisibility(ImageView.VISIBLE);
                mImage2.setImageBitmap(BitmapFactory.decodeFile(FileManager.getPath(getActivity().getApplicationContext(), type, uri)));
            } else if (mFile.size() == 3) {
                mImage3.setVisibility(ImageView.VISIBLE);
                mImage3.setImageBitmap(BitmapFactory.decodeFile(FileManager.getPath(getActivity().getApplicationContext(), type, uri)));
            } else if (mFile.size() == 4) {
                mImage4.setVisibility(ImageView.VISIBLE);
                mImage4.setImageBitmap(BitmapFactory.decodeFile(FileManager.getPath(getActivity().getApplicationContext(), type, uri)));
            } else if (mFile.size() == 5) {
                mImage5.setVisibility(ImageView.VISIBLE);
                mImage5.setImageBitmap(BitmapFactory.decodeFile(FileManager.getPath(getActivity().getApplicationContext(), type, uri)));
            } else if (mFile.size() == 6) {
                mImage6.setVisibility(ImageView.VISIBLE);
                mImage6.setImageBitmap(BitmapFactory.decodeFile(FileManager.getPath(getActivity().getApplicationContext(), type, uri)));
            } else if (mFile.size() == 7) {
                mImage7.setVisibility(ImageView.VISIBLE);
                mImage7.setImageBitmap(BitmapFactory.decodeFile(FileManager.getPath(getActivity().getApplicationContext(), type, uri)));
            } else if (mFile.size() == 8) {
                mImage8.setVisibility(ImageView.VISIBLE);
                mImage8.setImageBitmap(BitmapFactory.decodeFile(FileManager.getPath(getActivity().getApplicationContext(), type, uri)));
            } else if (mFile.size() == 9) {
                mImage9.setVisibility(ImageView.VISIBLE);
                mImage9.setImageBitmap(BitmapFactory.decodeFile(FileManager.getPath(getActivity().getApplicationContext(), type, uri)));
            }

        } else if (type == Template.Code.CAMERA_VIDEO_CODE) {
            mVideo.setVisibility(VideoView.VISIBLE);
            mVideo.setVideoPath(FileManager.getPath(getActivity().getApplicationContext(), type, uri));
            mController.setVisibility(ImageView.VISIBLE);
            mController.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (mVideo.isPlaying()) {
                        mController.setImageResource(R.drawable.ic_play);
                        mVideo.pause();
                    } else {

                        mController.setImageResource(R.drawable.ic_pause);
                        mVideo.start();
                    }
                }
            });
            mVideo.start();
        } else {

            File file = new File(FileManager.getPath(getActivity().getApplicationContext(), type, uri));
            int fileType = FileManager.fileType(file);
            if (fileType == Template.Code.CAMERA_IMAGE_CODE) {
                if (mFile.size() == 1) {
                    mImage.setVisibility(ImageView.VISIBLE);
                    mImage.setImageBitmap(BitmapFactory.decodeFile(FileManager.getPath(getActivity().getApplicationContext(), type, uri)));
                } else if (mFile.size() == 2) {
                    mImage2.setVisibility(ImageView.VISIBLE);
                    mImage2.setImageBitmap(BitmapFactory.decodeFile(FileManager.getPath(getActivity().getApplicationContext(), type, uri)));
                } else if (mFile.size() == 3) {
                    mImage3.setVisibility(ImageView.VISIBLE);
                    mImage3.setImageBitmap(BitmapFactory.decodeFile(FileManager.getPath(getActivity().getApplicationContext(), type, uri)));
                }
            } else if (fileType == Template.Code.CAMERA_VIDEO_CODE) {
                mVideo.setVisibility(VideoView.VISIBLE);
                mVideo.setVideoPath(FileManager.getPath(getActivity().getApplicationContext(), type, uri));
                mController.setVisibility(ImageView.VISIBLE);
                mController.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        if (mVideo.isPlaying()) {
                            mController.setImageResource(R.drawable.ic_play);
                            mVideo.pause();
                        } else {

                            mController.setImageResource(R.drawable.ic_pause);
                            mVideo.start();
                        }
                    }
                });
                mVideo.start();
            } else if (fileType == Template.Code.AUDIO_CODE) {
                mMediaPlayer = MediaPlayer.create(getActivity().getApplicationContext(), uri);
                mMediaPlayer.setLooping(true);
                mController.setVisibility(ImageView.VISIBLE);
                mController.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        if (mMediaPlayer.isPlaying()) {
                            mController.setImageResource(R.drawable.ic_play);
                            mMediaPlayer.pause();
                        } else {

                            mController.setImageResource(R.drawable.ic_pause);
                            mMediaPlayer.start();
                        }
                    }
                });
                mMediaPlayer.start();
            } else {
                mImage.setVisibility(ImageView.VISIBLE);
                mImage.setImageResource(R.drawable.ic_android_green_500_48dp);
            }

        }
    }

    //Mereset tampilan ke semula
    void resetView() {
        mUpload.setVisibility(Button.GONE);
        mImage.setVisibility(ImageView.GONE);
        mVideo.setVisibility(VideoView.GONE);
        mInfo.setVisibility(TextView.GONE);
        mInfo.setText("");
        mResponse.setText("");
        mAdd.setText("Add");
        mProgress.setVisibility(ProgressBar.GONE);
        mController.setVisibility(ImageView.GONE);
        mController.setImageResource(R.drawable.ic_pause);
        if (mVideo.isPlaying())
            mVideo.pause();
        if (mMediaPlayer != null && mMediaPlayer.isPlaying())
            mMediaPlayer.pause();
    }

    //Respon dari volley, untuk menampilkan keterengan upload, seperti error, message dari server
    void setResponse(Object response, VolleyError error) {
        String name = "aa0";
        String file1 = null;
        String file2 = null;
        String file3 = null;

        if (response == null) {
            mResponse.setText("Error1\n" + "null response");
            Log.d("ERROR:", String.valueOf(error));
        } else {
            if (String.valueOf(response).equals(Template.Query.VALUE_CODE_SUCCESS)){
                mResponse.setText("Success\n" + String.valueOf(response));
            }
            else{
                int lat = 700;
                String message = "Error Upload files..try again";
                try {

                    JSONObject jsonObject=new JSONObject((String) response);
                         lat=jsonObject.getInt("kode");

                         file1=jsonObject.getString("file1");
                         file2=jsonObject.getString("file2");
                         file3=jsonObject.getString("file3");
                         message=jsonObject.getString("pesan");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                file_names.clear();
                Log.d("names", file1+",,,"+file2+",,,"+file3);
                if (!file1.equals("kaka")){
                    file_names.add(file1);
                }
                if (!file2.equals("kaka")){
                    file_names.add(file2);
                }
                if (!file3.equals("kaka")){
                    file_names.add(file3);
                }
                Log.d("names1", file_names.get((file_names.size())-1));
                Log.d("size", String.valueOf(file_names.size()));
                mResponse.setText("Server Message\n" + message);

               /* Fragment fragment = new NewsRoomFragment();
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.add(R.id.content_frame, fragment, "newsroom");
                ft.addToBackStack("newsroom");
                ft.replace(R.id.content_frame, fragment);
                ft.commit(); */

            Log.d("ERROR", String.valueOf(response));}

        }
    }

    //Respon dari pengambilan data dari storage
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == Template.Code.FILE_MANAGER_CODE) {
                setFile(requestCode, data.getData());
                setView(requestCode, data.getData());
            } else {
                setFile(requestCode, mOutputUri);
                setView(requestCode, mOutputUri);
            }

        } else {
            resetView();
        }
    }

}