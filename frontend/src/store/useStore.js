import { create } from 'zustand';

const useStore = create((set, get) => ({
  // State
  breedStatistics: null,
  selectedBreed: null,
  healthConcerns: null,
  loading: false,
  error: null,
  
  // Actions
  setBreedStatistics: (stats) => set({ breedStatistics: stats }),
  
  setSelectedBreed: (breed) => set({ selectedBreed: breed }),
  
  setHealthConcerns: (concerns) => set({ healthConcerns: concerns }),
  
  setLoading: (loading) => set({ loading }),
  
  setError: (error) => set({ error }),
  
  clearError: () => set({ error: null }),
  
  // Computed values
  getTotalDogs: () => {
    const stats = get().breedStatistics;
    if (!stats) return 0;
    return Object.values(stats).reduce((sum, count) => sum + count, 0);
  },
  
  getTopBreeds: (limit = 5) => {
    const stats = get().breedStatistics;
    if (!stats) return [];
    
    return Object.entries(stats)
      .sort(([, a], [, b]) => b - a)
      .slice(0, limit)
      .map(([breed, count]) => ({ breed, count }));
  }
}));

export default useStore;
