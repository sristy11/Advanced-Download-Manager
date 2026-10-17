# Advanced Download Manager

A JavaFX desktop application for downloading multiple files at once. It uses Java multithreading and concurrency to run downloads in parallel, sorts files into categories by extension, and queues downloads beyond a user-defined limit.

## Features

- **Simultaneous downloads:** several files download in parallel, each on its own worker thread.
- **File categorization:** downloaded files are organized into categories based on their file extensions (e.g., documents, videos, archives, images).
- **Customizable concurrency limit:** the user sets the maximum number of downloads that can run at the same time.
- **Download queue:** downloads beyond the limit wait in a queue and start automatically when a slot becomes free.

## Tech Stack

- Java
- JavaFX
- Java concurrency utilities (multithreading, thread-safe task handling)
- Build tool: Maven

## How It Works

1. The user adds a download URL.
2. If the number of active downloads is below the configured limit, the download starts immediately on a separate thread.
3. Otherwise, it is placed in the download queue.
4. When a download finishes, the next queued download starts.
5. Completed files are saved into a folder matching their file extension category.

```

## Concepts Demonstrated

- Multithreading and concurrent task execution in Java
- Thread-safe queue management
- Updating a JavaFX UI safely from background threads
- Separating UI, download logic, and file handling
