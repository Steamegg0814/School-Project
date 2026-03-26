import { useQuery } from 'react-query';
import { statisticsApi, retryWithBackoff } from '../services/api';
import useStore from '../store/useStore';
import { useEffect } from 'react';

export const useDogData = () => {
  const { setBreedStatistics, setError, setLoading } = useStore();
  
  const {
    data,
    error,
    isLoading,
    isError,
    refetch
  } = useQuery(
    'breedStatistics',
    () => retryWithBackoff(() => statisticsApi.getBreedStatistics()),
    {
      staleTime: 5 * 60 * 1000, // 5 minutes
      retry: 3,
      retryDelay: (attemptIndex) => Math.min(1000 * 2 ** attemptIndex, 30000),
      onSuccess: (response) => {
        if (response.data) {
          setBreedStatistics(response.data);
          setError(null);
        }
      },
      onError: (err) => {
        setError(err.message || 'Failed to fetch breed statistics');
      }
    }
  );
  
  useEffect(() => {
    setLoading(isLoading);
  }, [isLoading, setLoading]);
  
  return {
    breedStatistics: data?.data || null,
    loading: isLoading,
    error: isError ? error : null,
    refetch
  };
};

export const useHealthConcerns = (breed) => {
  const { setHealthConcerns, setError } = useStore();
  
  const {
    data,
    error,
    isLoading,
    isError
  } = useQuery(
    ['healthConcerns', breed],
    () => retryWithBackoff(() => statisticsApi.getHealthConcerns(breed)),
    {
      enabled: !!breed,
      staleTime: 10 * 60 * 1000, // 10 minutes
      retry: 2,
      onSuccess: (response) => {
        if (response.data) {
          setHealthConcerns(response.data.healthConcerns);
          setError(null);
        }
      },
      onError: (err) => {
        setError(err.message || 'Failed to fetch health concerns');
      }
    }
  );
  
  return {
    healthConcerns: data?.data?.healthConcerns || null,
    loading: isLoading,
    error: isError ? error : null
  };
};
