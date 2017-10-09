package catalog.neo4j.category;

import lombok.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import static java.util.stream.Collectors.toList;

@Service
@Value
public class Neo4jCategoryService {
    private final Neo4jCategoryRepository neo4jCategoryRepository;

    public CategoryResponse createCategory(CategoryRequest categoryRequest) {
        Category category = mapRequestToDomainObject(categoryRequest);

        if (categoryRequest.getParentCategoryId() != null) {
            Optional<Category> parent = neo4jCategoryRepository.findById(categoryRequest.getParentCategoryId());
            if (parent.isPresent()) {
                category.setParent(parent.get());
            } else {
                throw new DataIntegrityViolationException("Foreign key to parent category not valid");
            }
        }

        Category savedCategory = neo4jCategoryRepository.save(category);

        return mapDomainObjectToResponse(savedCategory);
    }

    private Category mapRequestToDomainObject(CategoryRequest categoryRequest) {
        Category category = new Category();
        category.setName(categoryRequest.getName());
        return category;
    }

    public Optional<CategoryResponseWithSubCategories> updateCategory(
            long categoryId, CategoryRequest categoryRequest) {
        List<Category> subCategories = neo4jCategoryRepository.findByParentId(categoryId);
        return neo4jCategoryRepository.findById(categoryId)
                .map(category -> updateAttributes(category, categoryRequest))
                .map(neo4jCategoryRepository::save)
                .map(category -> mapDomainObjectToResponse(category, subCategories));
    }

    private Category updateAttributes(Category category, CategoryRequest categoryRequest) {
        category.setName(categoryRequest.getName());
        return category;
    }

    public Optional<CategoryResponseWithSubCategories> loadCategory(long categoryId) {
        List<Category> subCategories = neo4jCategoryRepository.findByParentId(categoryId);
        return neo4jCategoryRepository.findById(categoryId)
                .map(category -> mapDomainObjectToResponse(category, subCategories));
    }

    private CategoryResponseWithSubCategories mapDomainObjectToResponse(
            Category category, List<Category> subCategories) {
        return new CategoryResponseWithSubCategories(
                category.getId(),
                category.getName(),
                subCategories.stream()
                        .map(this::mapDomainObjectToResponse)
                        .collect(toList())
        );
    }

    private CategoryResponse mapDomainObjectToResponse(Category category) {
        return new CategoryResponse(category.getId(), category.getName());
    }

    public List<CategoryResponse> loadCategoryPath(Long categoryId) {
        return neo4jCategoryRepository.loadCategoryPath(categoryId)
                .stream()
                .map(this::mapDomainObjectToResponse)
                .collect(toList());
    }
}
