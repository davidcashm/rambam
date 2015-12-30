package com.ezmashay.rambam;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

public class Rambam extends AppCompatActivity {

    // Each entry is a 2-element array - file name and description
    public class Entry {
        private String f;
        private String t;
        public Entry(String file, String title) {
            f = file;
            t = title;
        }
        public String toString() {
            return t;
        }
        public String htm_name() {
            return f;
        }
    }

    ArrayList<Entry> files = new ArrayList<>();
    ArrayList<String> books = new ArrayList<>();
    // Parallel array to books, containing number of chapters in the book
    ArrayList<Integer> book_num_chapters = new ArrayList<>();
    // Parallel array to books, containing the index into files for the first chapter of the book
    ArrayList<Integer> book_first_chapter = new ArrayList<>();
    ListView listView;

    int current_book_idx = -1;

    // Does Java have enums?
    static final int STATE_MAIN_MENU = 0;
    static final int STATE_BOOK_MENU = 1;
    int state = STATE_MAIN_MENU;

    private void addEntry(String[] file_and_title) {
        String title = file_and_title[1];
        String[] book_and_chapter = title.split("-", 2);
        // My file has trailing whitespace on some lines, which causes books to look different.
        String book = book_and_chapter[0].trim();
        String chapter = book_and_chapter.length == 1 ? "Overview" : book_and_chapter[1];
        String prev_book = books.isEmpty() ? "" : books.get(books.size()-1);
        files.add(new Entry(file_and_title[0], chapter));
        if (!book.equals(prev_book)) {
            books.add(book);
            book_num_chapters.add(1);
            book_first_chapter.add(files.size() - 1);
        }
        else {
            // Increment the last entry in book_num_chapters to indicate addition of a new chapter.
            // This is obscene.  Is there a better way?
            book_num_chapters.set(book_num_chapters.size() - 1, book_num_chapters.get(book_num_chapters.size()-1) + 1);
        }
    }

    private void set_chapter_view(int book_idx) {
        Entry[] values = new Entry[book_num_chapters.get(book_idx)];
        final Context context = this;
        if (values.length == 1) {
            // Special case: There's only one page, so just go directly to it.
            int idx = book_first_chapter.get(book_idx);
            String file = files.get(idx).htm_name();
            Intent i = new Intent(context, TheWebActivity.class);
            i.putExtra("PAGE", file);
            startActivity(i);
        }
        else {
            for (int i = 0; i < values.length; i++) {
                int idx = i + book_first_chapter.get(book_idx);
                values[i] = files.get(idx);
            }
            ArrayAdapter<Entry> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1,
                    android.R.id.text1, values);

            listView.setAdapter(adapter);

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Entry e = (Entry) listView.getItemAtPosition(position);
                    String file = e.htm_name();
                    Intent i = new Intent(context, TheWebActivity.class);
                    i.putExtra("PAGE", file);
                    startActivity(i);
                }
            });

            state = STATE_BOOK_MENU;
        }
    }

    public void set_main_menu_view() {
        ArrayList<String> values = books;

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1,
                android.R.id.text1, values);

        listView.setAdapter(adapter);

        final Context context = this;
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                set_chapter_view(position);
            }
        });

        state = STATE_MAIN_MENU;
    }

    @Override
    public void onBackPressed() {
        if (state == STATE_BOOK_MENU)
        {
            set_main_menu_view();
        }
        else {
            // Already in main menu.  Just let the app exit.
            super.onBackPressed();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rambam);
        // TODO: Think about the toolbar.  The default one seems very intrusive.
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        int readme_id = getResources().getIdentifier("titles.txt", "raw", getPackageName());

        // Load up the list of content
        listView = (ListView) findViewById(R.id.list);

        InputStream ins = getResources().openRawResource(R.raw.titles);
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(ins, "Windows-1255"));
            for (String line = reader.readLine(); line != null; line = reader.readLine()) {
                String[] s = line.split("\\t");
                if (s.length == 2) {
                    addEntry(s);
                }
            }
        }
        catch (IOException e) {
            // I wonder what an appropriate response would be?
            // Just give some sort of error message and exit, I guess
        }

        set_main_menu_view();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_rambam, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }

        return super.onOptionsItemSelected(item);
    }
}
