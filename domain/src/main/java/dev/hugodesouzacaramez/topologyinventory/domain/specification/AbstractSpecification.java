package dev.hugodesouzacaramez.topologyinventory.domain.specification;

public abstract sealed class AbstractSpecification<T> implements Specification<T> permits
        AndSpecification,
        CIDRSpecification,
        EmptyNetworkSpec,
        EmptyRouterSpec,
        EmptySwitchSpec,
        NetworkAmountSpec,
        NetworkAvailabilitySpec,
        SameCountrySpec,
        SameIpSpec
{

    public abstract boolean isSatisfiedBy(T t);

    public abstract void check(T t) throws GenericSpecificationException;

    public Specification<T> and(final Specification<T> specification) {
        return new AndSpecification<T>(this, specification);
    }
}
