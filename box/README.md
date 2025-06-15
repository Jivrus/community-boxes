# Box

This folder contains a project that uses the `box` library as a local dependency and provides scripts to build and run a server.

---

## Prerequisites

- [Node.js](https://nodejs.org/) (v22 or above recommended)
- [npm](https://www.npmjs.com/)

---

## Setup & Usage

### 1. Install the Box Package

Navigate to the `box` folder and install the local `box` package:

```sh
cd box
npm install "box-1.4.7.tgz"
```

---

### 2. To add a new Box

- Create a folder in `meta/schema/box/${box_id}` (replace `${box_id}` with your box's ID).
- Add your function and attributes JSON files inside this folder.
- Create a `${box_id}.json` file in the `meta/schema/box` folder.

---

### 3. Build the Meta

After adding or updating any meta files, run:

```sh
npm run build:box
```

> **Note:** Any changes made to meta require rebuilding with this command.

---

### 4. Run the Box Server

Start the server with:

```sh
npm run start:box
```

---

### 5. Test Box APIs

- Open [Postman](https://www.postman.com/) or any API client.
- Test your box API endpoints as needed.

---