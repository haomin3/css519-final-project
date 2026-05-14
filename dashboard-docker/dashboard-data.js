const PRODUCT_OE_URL = "http://localhost:8080/oe";

let cachedOeData = null;

async function fetchOeData() {
    if (cachedOeData) {
        return cachedOeData;
    }

    const response = await fetch(PRODUCT_OE_URL);

    if (!response.ok) {
        throw new Error("Could not load OE data from product");
    }

    cachedOeData = await response.json();
    return cachedOeData;
}

window.DashboardData = {
    async getSummaryData() {
        const data = await fetchOeData();
        return data.summary;
    },

    async getAuthChartData(range = "hourly") {
        const data = await fetchOeData();
        return data.authChartData[range];
    },

    async getErrorChartData(range = "hourly") {
        const data = await fetchOeData();
        return data.errorChartData[range];
    },

    async getCustomerRiskMetrics() {
        const data = await fetchOeData();
        return data.customerRiskMetrics;
    },

    async getEventData() {
        const data = await fetchOeData();
        return data.events;
    }
};