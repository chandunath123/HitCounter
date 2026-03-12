class Main {
    private static final int WINDOW = 300;

    private final int[] hits = new int[WINDOW];
    private int latestTimestamp;
    private int totalHits;

    public synchronized void hit(int timestamp) {
        validateTimestamp(timestamp);
        advanceWindow(timestamp);
        hits[timestamp % WINDOW]++;
        totalHits++;
    }

    public synchronized int getHits(int timestamp) {
        validateTimestamp(timestamp);
        advanceWindow(timestamp);
        return totalHits;
    }

    private void validateTimestamp(int timestamp) {
        if (timestamp < latestTimestamp) {
            throw new IllegalArgumentException("Timestamps must be non-decreasing.");
        }
    }

    private void advanceWindow(int timestamp) {
        if (timestamp == latestTimestamp) {
            return;
        }

        int elapsed = timestamp - latestTimestamp;
        if (elapsed >= WINDOW) {
            clearAll();
        } else {
            for (int second = latestTimestamp + 1; second <= timestamp; second++) {
                int index = second % WINDOW;
                totalHits -= hits[index];
                hits[index] = 0;
            }
        }

        latestTimestamp = timestamp;
    }

    private void clearAll() {
        for (int i = 0; i < WINDOW; i++) {
            hits[i] = 0;
        }
        totalHits = 0;
    }

    public static void main(String[] args) {
        Main counter = new Main();

        counter.hit(1);
        counter.hit(1);
        counter.hit(2);

        System.out.println(counter.getHits(2));    // 3

        counter.hit(301);
        counter.hit(301);
        counter.hit(301);
        counter.hit(301);

        System.out.println(counter.getHits(301));  // 5
    }
}
