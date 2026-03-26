import axios from 'axios';

const API_BASE_URL = import.meta.env.VITE_API_URL || '/api';

const apiClient = axios.create({
  baseURL: API_BASE_URL,
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json'
  }
});

// Error handling interceptor
apiClient.interceptors.response.use(
  response => response,
  error => {
    const errorMessage = error.response?.data?.message || error.message || 'Unknown error';
    console.error('API Error:', errorMessage);
    return Promise.reject({
      code: error.response?.data?.error || 'UNKNOWN_ERROR',
      message: errorMessage,
      status: error.response?.status
    });
  }
);

// Functional approach to API calls
const handleApiCall = async (apiFunction) => {
  try {
    const response = await apiFunction();
    return { data: response.data, error: null };
  } catch (error) {
    return { data: null, error };
  }
};

export const statisticsApi = {
  getBreedStatistics: () => 
    handleApiCall(() => apiClient.get('/statistics/breeds')),
  
  getHealthConcerns: (breed) => 
    handleApiCall(() => apiClient.get(`/statistics/health-concerns/${breed}`))
};

export const retryWithBackoff = async (fn, retries = 3, delay = 1000) => {
  try {
    return await fn();
  } catch (error) {
    if (retries === 0) throw error;
    await new Promise(resolve => setTimeout(resolve, delay));
    return retryWithBackoff(fn, retries - 1, delay * 2);
  }
};

export default apiClient;
